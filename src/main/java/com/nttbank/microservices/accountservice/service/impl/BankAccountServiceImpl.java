package com.nttbank.microservices.accountservice.service.impl;

import com.nttbank.microservices.accountservice.action.IDepositable;
import com.nttbank.microservices.accountservice.action.IOpenable;
import com.nttbank.microservices.accountservice.action.IWithdrawable;
import com.nttbank.microservices.accountservice.factory.BackAccountFactory;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.model.response.MovementResponse;
import com.nttbank.microservices.accountservice.model.response.TransferResponse;
import com.nttbank.microservices.accountservice.repo.IBankAccountRepo;
import com.nttbank.microservices.accountservice.service.BankAccountService;
import com.nttbank.microservices.accountservice.service.CreditCardService;
import com.nttbank.microservices.accountservice.service.CustomerService;
import com.nttbank.microservices.accountservice.service.MovementService;
import com.nttbank.microservices.accountservice.util.Constants;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

  private static final Logger logger = LoggerFactory.getLogger(BankAccountServiceImpl.class);
  private final IBankAccountRepo repo;
  private final CustomerService customerService;
  private final CreditCardService creditCardService;
  private final MovementService movementService;
  private final TransactionalOperator transactionalOperator;

  @Override
  public Mono<BankAccount> save(BankAccount account) {
    logger.info("Initiating the open bank account process.");
    return customerService.findCustomerById(account.getCustomerId())
        .flatMap(customer -> {
          BankAccount bankAccount = BackAccountFactory.createAccount(account.getAccountType(),
              account);
          if (!(bankAccount instanceof IOpenable)) {
            logger.error(Constants.INVALID_ACCOUNT_TYPE);
            return Mono.error(
                new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    Constants.INVALID_ACCOUNT_TYPE));
          }
          return hasLeastOneCreditCard(bankAccount, customer.getProfile())
              .flatMap(b ->
                  repo.countByCustomerIdAndAccountType(customer.getId(),
                      bankAccount.getAccountType())
              ).flatMap(totalAccounts -> {
                ((IOpenable) bankAccount).openAccount(totalAccounts, customer.getType());
                return repo.save(bankAccount);
              });
        });
  }

  private Mono<BankAccount> hasLeastOneCreditCard(BankAccount bankAccount, String customerProfile) {
    Map<String, Boolean> accountProfileMap =
        Map.of("saving:vip", true, "checking:pyme", true);
    if (accountProfileMap.get(bankAccount.getAccountType() + ":" + customerProfile) == null) {
      return Mono.just(bankAccount);
    }
    logger.info("Calling method to retrieve all active credit cards from customer {}",
        bankAccount.getCustomerId());
    return creditCardService.totalActiveCreditsCardsByCustomer(bankAccount.getCustomerId(),
            "active")
        .flatMap(totalActiveCredits -> {
          logger.info("Total active credit cards for customer: {}", totalActiveCredits);
          if (totalActiveCredits == 0) {
            logger.info(Constants.NO_ACTIVE_CREDIT_CARDS);
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                Constants.NO_ACTIVE_CREDIT_CARDS));
          }
          return Mono.just(bankAccount);
        });
  }

  @Override
  public Mono<BankAccount> update(BankAccount t) {
    return repo.save(t);
  }

  @Override
  public Flux<BankAccount> findAll() {
    return repo.findAll();
  }

  @Override
  public Mono<BankAccount> findById(String accountId) {
    return repo.findById(accountId);
  }

  @Override
  public Mono<Void> delete(String accountId) {
    return repo.deleteById(accountId);
  }

  @Override
  public Mono<BankAccount> withdraw(String accountId, BigDecimal amount) {
    logger.info("Initiating the withdraw process.");
    return repo.findById(accountId)
        .flatMap(b -> Mono.just(BackAccountFactory.createAccount(b.getAccountType(), b)))
        .flatMap(b -> {
          if (b instanceof IWithdrawable) {
            ((IWithdrawable) b).withdraw(amount);
            return repo.save(b)
                .flatMap(bankAccount ->
                    movementService.saveMovement(MovementResponse.builder()
                        .customerId(bankAccount.getCustomerId())
                        .accountId(accountId)
                        .balanceAfterMovement(bankAccount.getBalance())
                        .amount(amount)
                        .type(Constants.WITHDRAWAL)
                        .timestamp(LocalDateTime.now())
                        .build()
                    ).thenReturn(bankAccount));
          }
          return Mono.error(
              new IllegalStateException("You cannot withdraw from this account: " + b.getId()));
        })
        .as(transactionalOperator::transactional)
        .onErrorResume(e -> {
          return Mono.error(new IllegalStateException("Withdraw failed: " + e.getMessage(), e));
        });
  }

  @Override
  public Mono<BankAccount> deposit(String accountId, BigDecimal amount) {
    logger.info("Initiating the deposit process.");
    return repo.findById(accountId)
        .flatMap(b -> Mono.just(BackAccountFactory.createAccount(b.getAccountType(), b)))
        .flatMap(b -> {
          if (b instanceof IDepositable) {
            ((IDepositable) b).deposit(amount);
            return repo.save(b).flatMap(bankAccount -> {
              return movementService.saveMovement(MovementResponse.builder()
                  .customerId(bankAccount.getCustomerId())
                  .accountId(accountId)
                  .balanceAfterMovement(bankAccount.getBalance())
                  .amount(amount)
                  .type(Constants.DEPOSIT)
                  .timestamp(LocalDateTime.now())
                  .build()
              ).thenReturn(bankAccount);
            });
          }
          return Mono.error(
              new IllegalStateException("You cannot deposit to this account: " + b.getId()));
        }).onErrorResume(e -> {
          return Mono.error(new IllegalStateException("Deposit failed: " + e.getMessage(), e));
        });
  }

  @Override
  public Mono<TransferResponse> transfer(String fromAccountId, String toAccountId,
      BigDecimal amount) {
    logger.info("Initiating the transfer process.");
    return withdraw(fromAccountId, amount)
        .flatMap(fromAccount -> deposit(toAccountId, amount).map(
            toAccount -> new TransferResponse(fromAccountId, toAccountId, amount)))
        .onErrorResume(
            e -> Mono.error(
                new IllegalStateException("Transfer failed: " + e.getMessage(), e)));
  }
}
