package com.nttbank.microservices.accountservice.service.impl;

import com.nttbank.microservices.accountservice.action.IDepositable;
import com.nttbank.microservices.accountservice.action.IOpenable;
import com.nttbank.microservices.accountservice.action.IWithdrawable;
import com.nttbank.microservices.accountservice.factory.BackAccountFactory;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.model.response.CustomerResponse;
import com.nttbank.microservices.accountservice.model.response.TransferResponse;
import com.nttbank.microservices.accountservice.repo.IBankAccountRepo;
import com.nttbank.microservices.accountservice.service.BankAccountService;
import com.nttbank.microservices.accountservice.service.CustomerService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of {@link BankAccountService} to handle business logic for bank accounts.
 * This service interacts with the {@link IBankAccountRepo}
 * repository and the {@link CustomerService}.
 */
@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

  private final IBankAccountRepo repo;
  private final CustomerService customerService;

  @Override
  public Mono<BankAccount> save(BankAccount account) {
    Mono<CustomerResponse> customer = customerService.findCustomerById(account.getCustomerId());
    Mono<BankAccount> bankAccount = Mono.just(
        BackAccountFactory.createAccount(account.getAccountType(), account));

    return customer.flatMap(c -> bankAccount.flatMap(
        b -> repo.findByCustomerIdAndAccountType(c.getId(), b.getAccountType()).count()
            .flatMap(totalAccounts -> {
              if (b instanceof IOpenable) {
                if (((IOpenable) b).openAccount(totalAccounts, c.getType())) {
                  return repo.save(b);
                }
                return Mono.error(new IllegalArgumentException(
                    "Customer type :" + c.getType() + " cannot open an account of type"
                        + b.getAccountType()));
              }
              return Mono.error(new IllegalStateException(
                  "You cannot open an account of type " + b.getAccountType()));
            })));
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
    return repo.findById(accountId)
        .flatMap(b -> Mono.just(BackAccountFactory.createAccount(b.getAccountType(), b)))
        .flatMap(b -> {
          if (b instanceof IWithdrawable) {
            ((IWithdrawable) b).withdraw(amount);
            return repo.save(b);
            //TODO: registrar la transaccion (withdraw) | llamado endpoint de transacciones
          }
          return Mono.error(
              new IllegalStateException("You cannot withdraw from this account: " + b.getId()));
        });
  }

  @Override
  public Mono<BankAccount> deposit(String accountId, BigDecimal amount) {
    return repo.findById(accountId)
        .flatMap(b -> Mono.just(BackAccountFactory.createAccount(b.getAccountType(), b)))
        .flatMap(b -> {
          if (b instanceof IDepositable) {
            ((IDepositable) b).deposit(amount);
            return repo.save(b);
            //TODO: registrar la transaccion (deposit) | llamado endpoint de transacciones
          }
          return Mono.error(
              new IllegalStateException("You cannot deposit to this account: " + b.getId()));
        });
  }

  @Override
  public Mono<TransferResponse> transfer(String fromAccountId, String toAccountId,
      BigDecimal amount) {
    return withdraw(fromAccountId, amount)
        .flatMap(fromAccount -> deposit(toAccountId, amount)
            .map(toAccount -> new TransferResponse(fromAccountId, toAccountId, amount)))
        .onErrorResume(e -> {
          return Mono.error(new IllegalStateException("Transfer failed: " + e.getMessage(), e));
        });
  }
}
