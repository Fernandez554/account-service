package com.nttbank.microservices.accountservice.service.impl;

import com.nttbank.microservices.accountservice.action.IDepositable;
import com.nttbank.microservices.accountservice.action.IOpenable;
import com.nttbank.microservices.accountservice.action.IWithdrawable;
import com.nttbank.microservices.accountservice.factory.BackAccountFactory;
import com.nttbank.microservices.accountservice.model.entity.AccountTransactions;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.model.entity.MonthlyTransactionSummary;
import com.nttbank.microservices.accountservice.model.response.CustomerResponse;
import com.nttbank.microservices.accountservice.model.response.TransferResponse;
import com.nttbank.microservices.accountservice.repo.IAccountTransactionRepo;
import com.nttbank.microservices.accountservice.repo.IBankAccountRepo;
import com.nttbank.microservices.accountservice.service.BankAccountService;
import com.nttbank.microservices.accountservice.service.CreditCardService;
import com.nttbank.microservices.accountservice.service.CustomerService;
import com.nttbank.microservices.accountservice.util.AccountUtils;
import com.nttbank.microservices.accountservice.util.Constants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of {@link BankAccountService} to handle business logic for bank accounts. This
 * service interacts with the {@link IBankAccountRepo} repository and the {@link CustomerService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

  private final IBankAccountRepo accountRepo;
  private final IAccountTransactionRepo transactionRepo;
  private final CustomerService customerService;
  private final CreditCardService creditCardService;
  private final TransactionalOperator transactionalOperator;

  @Override
  public Mono<BankAccount> save(BankAccount account) {
    log.info("Initiating the open bank account process.");
    return customerService.findCustomerById(account.getCustomerId())
        .flatMap(customer -> {
          BankAccount bankAccount = BackAccountFactory.createAccount(account.getAccountType(),
              account);
          if (!(bankAccount instanceof IOpenable)) {
            log.warn(Constants.INVALID_ACCOUNT_TYPE);
            return Mono.error(
                new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    Constants.INVALID_ACCOUNT_TYPE));
          }
          return hasLeastOneCreditCard(bankAccount, customer.getProfile())
              .flatMap(b -> isABusinessAccount(b, customer))
              .flatMap(b ->
                  accountRepo.countByCustomerIdAndAccountType(customer.getId(),
                      bankAccount.getAccountType())
              ).flatMap(totalAccounts -> {
                ((IOpenable) bankAccount).openAccount(totalAccounts, customer.getType());
                return accountRepo.save(bankAccount);
              });
        });
  }

  private Mono<BankAccount> isABusinessAccount(BankAccount b, CustomerResponse customer) {
    log.info("Checking if the account is a business account.");
    return Mono.just(b);
  }

  private Mono<BankAccount> hasLeastOneCreditCard(BankAccount bankAccount, String customerProfile) {
    log.info("Checking if the customer has at least one active credit card.");

    if (!AccountUtils.CHECK_CUSTOMER_CREDIT_CARD
        .test(bankAccount.getAccountType(), customerProfile)) {
      return Mono.just(bankAccount);
    }
    return creditCardService.totalActiveCreditsCardsByCustomer(bankAccount.getCustomerId(),
            AccountUtils.CREDIT_CARD_STATUS_ACTIVE)
        .flatMap(totalActiveCredits -> {
          if (totalActiveCredits == 0) {
            log.info(Constants.NO_ACTIVE_CREDIT_CARDS);
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                Constants.NO_ACTIVE_CREDIT_CARDS));
          }
          return Mono.just(bankAccount);
        });
  }

  @Override
  public Mono<BankAccount> update(BankAccount t) {
    return accountRepo.save(t);
  }

  @Override
  public Flux<BankAccount> findAll() {
    return accountRepo.findAll();
  }

  @Override
  public Mono<BankAccount> findById(String accountId) {
    return accountRepo.findById(accountId).flatMap(bankAccount ->
        transactionRepo.findAllByAccountId(accountId)
            .collectList()
            .map(transactions -> {
              bankAccount.setLstTransactions(transactions);
              return bankAccount;
            })
    );
  }

  @Override
  public Mono<Void> delete(String accountId) {
    return accountRepo.deleteById(accountId);
  }


  @Override
  public Mono<AccountTransactions> withdraw(String accountId, BigDecimal amount) {
    log.info("Initiating the withdraw process.");
    return accountRepo.findById(accountId)
        .flatMap(b -> Mono.just(BackAccountFactory.createAccount(b.getAccountType(), b)))
        .flatMap(b -> {
          if (b instanceof IWithdrawable) {
            ((IWithdrawable) b).withdraw(amount);
            return addOneTransaction(b)
                .flatMap(bankAccount -> {
                  return saveTransaction(bankAccount, amount.setScale(2, RoundingMode.HALF_UP),
                      Constants.WITHDRAWAL)
                      .flatMap(transactionOne -> {
                        return checkAndHandleMaxTransactions(bankAccount,
                            amount.setScale(2, RoundingMode.HALF_UP))
                            .flatMap(transaction -> {
                              transactionOne.setBalanceAfterMovement(bankAccount.getBalance());
                              return accountRepo.save(bankAccount).thenReturn(transaction);
                            })
                            .thenReturn(transactionOne);
                      });
                });
          }
          return Mono.error(
              new IllegalStateException("You cannot withdraw from this account: " + b.getId()));
        })
        .as(transactionalOperator::transactional)
        .onErrorResume(e -> {
          return Mono.error(new IllegalStateException("Withdraw failed: " + e.getMessage(), e));
        });
  }

  public Mono<AccountTransactions> deposit(String accountId, BigDecimal amount) {
    log.info("Initiating the deposit process.");
    return accountRepo.findById(accountId)
        .flatMap(b -> Mono.just(BackAccountFactory.createAccount(b.getAccountType(), b)))
        .flatMap(b -> {
          if (b instanceof IDepositable) {
            ((IDepositable) b).deposit(amount);
            return addOneTransaction(b)
                .flatMap(bankAccount -> {
                  return saveTransaction(bankAccount, amount.setScale(2, RoundingMode.HALF_UP),
                      Constants.DEPOSIT)
                      .flatMap(transactionOne -> {
                        return checkAndHandleMaxTransactions(bankAccount,
                            amount.setScale(2, RoundingMode.HALF_UP))
                            .flatMap(transaction -> {
                              transactionOne.setBalanceAfterMovement(bankAccount.getBalance());
                              return accountRepo.save(bankAccount).thenReturn(transaction);
                            })
                            .thenReturn(transactionOne);
                      });
                });
          }
          return Mono.error(
              new IllegalStateException("You cannot deposit to this account: " + b.getId()));
        })
        .onErrorResume(e -> {
          return Mono.error(new IllegalStateException("Deposit failed: " + e.getMessage(), e));
        });
  }


  private Mono<BankAccount> addOneTransaction(BankAccount account) {
    LocalDateTime now = LocalDateTime.now();
    int currentMonth = now.getMonthValue();
    int currentYear = now.getYear();

    return Mono.just(account)
        .map(b -> {
          MonthlyTransactionSummary updatedSummary = Optional.ofNullable(
                  b.getMonthlyTransactionSummary())
              .filter(summary ->
                  summary.getMonth() == currentMonth && summary.getYear() == currentYear)
              .map(summary -> summary.toBuilder()
                  .numberOfTransactions(summary.getNumberOfTransactions() + 1)
                  .build())
              .orElseGet(() -> MonthlyTransactionSummary.builder()
                  .month(currentMonth)
                  .year(currentYear)
                  .numberOfTransactions(1)
                  .build());
          return b.toBuilder()
              .monthlyTransactionSummary(updatedSummary)
              .build();
        });
  }

  private Mono<BankAccount> checkAndHandleMaxTransactions(BankAccount account, BigDecimal amount) {

    return Mono.defer(() -> {
      MonthlyTransactionSummary summary = account.getMonthlyTransactionSummary();
      int maxTransactions = account.getMaxMonthlyTrans();
      BigDecimal transactionFee = Optional.ofNullable(account.getTransactionFee())
          .map(fee -> fee.divide(BigDecimal.valueOf(100), 2,
              RoundingMode.HALF_UP))
          .orElse(BigDecimal.ZERO);
      if (summary != null && summary.getNumberOfTransactions() >= maxTransactions) {
        BigDecimal totalAmountWithFee = amount.multiply(transactionFee)
            .setScale(2, RoundingMode.HALF_UP);
        if (totalAmountWithFee.compareTo(BigDecimal.ZERO) == 0) {
          return Mono.just(account);
        }
        account.setBalance(
            account.getBalance()
                .subtract(totalAmountWithFee)
                .setScale(2, RoundingMode.HALF_UP)
        );
        return saveTransaction(account, totalAmountWithFee, Constants.FEES)
            .thenReturn(account);
      }
      return Mono.just(account);
    });
  }

  private Mono<AccountTransactions> saveTransaction(BankAccount account, BigDecimal amount,
      String action) {
    log.info("Saving the {} transaction.", action);
    return transactionRepo.save(AccountTransactions.builder()
        .customerId(account.getCustomerId())
        .accountId(account.getId())
        .balanceAfterMovement(account.getBalance())
        .amount(amount)
        .type(action)
        .createdAt(LocalDateTime.now())
        .build()
    );
  }

  @Override
  public Mono<TransferResponse> transfer(String fromAccountId, String toAccountId,
      BigDecimal amount) {
    log.info("Initiating the transfer process.");
    return withdraw(fromAccountId, amount)
        .flatMap(fromAccount -> deposit(toAccountId, amount).map(
            toAccount -> new TransferResponse(fromAccountId, toAccountId,
                amount.setScale(2, RoundingMode.HALF_UP), LocalDateTime.now())))
        .onErrorResume(
            e -> Mono.error(
                new IllegalStateException("Transfer failed: " + e.getMessage(), e)));
  }

  @Override
  public Mono<BankAccount> saveSigner(String accountId, String signerId) {
    log.info("Initiating the save signer process.");
    return accountRepo.findById(accountId)
        .flatMap(account -> customerService.findCustomerById(signerId)
            .flatMap(customer -> {
              AccountUtils.addElementToSet(account, signerId, BankAccount::getLstSigners,
                  BankAccount::setLstSigners);
              return accountRepo.save(account);
            }));
  }

  @Override
  public Mono<BankAccount> deleteSigner(String accountId, String signerId) {
    log.info("Deleting the signer from the account.");
    return accountRepo.findById(accountId)
        .flatMap(account -> customerService.findCustomerById(signerId)
            .flatMap(customer -> {
              AccountUtils.removeElementToSet(account, signerId, BankAccount::getLstSigners,
                  BankAccount::setLstSigners);
              return accountRepo.save(account);
            }));
  }

  @Override
  public Mono<BankAccount> saveHolder(String accountId, String holderId) {
    log.info("Initiating the save holder process.");
    return accountRepo.findById(accountId)
        .flatMap(account -> customerService.findCustomerById(holderId)
            .flatMap(customer -> {
              AccountUtils.addElementToSet(account, holderId, BankAccount::getLstHolders,
                  BankAccount::setLstHolders);
              return accountRepo.save(account);
            }));
  }

  @Override
  public Mono<BankAccount> deleteHolder(String accountId, String holderId) {
    log.info("Deleting the holder from the account.");
    return accountRepo.findById(accountId)
        .flatMap(account -> customerService.findCustomerById(holderId)
            .flatMap(customer -> {
              AccountUtils.removeElementToSet(account, holderId, BankAccount::getLstHolders,
                  BankAccount::setLstHolders);
              return accountRepo.save(account);
            }));
  }


  @Override
  public Flux<AccountTransactions> findAccountTransactions(String accountId) {
    return transactionRepo.findAllByAccountId(accountId);
  }


}
