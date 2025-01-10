package com.nttbank.microservices.accountservice.service;

import com.nttbank.microservices.accountservice.model.entity.AccountTransactions;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.model.response.TransferResponse;
import java.math.BigDecimal;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interface for managing bank account operations. This defines the various actions that can
 * be performed on bank accounts, including saving, updating, finding, deleting, withdrawing,
 * depositing, and transferring funds.
 */
public interface BankAccountService {

  Flux<BankAccount> findAll();

  Mono<BankAccount> findById(String accountId);

  Mono<BankAccount> save(BankAccount t);

  Mono<BankAccount> update(BankAccount t);

  Mono<Void> delete(String accountId);

  Mono<AccountTransactions> withdraw(String accountId, BigDecimal amount);

  Mono<AccountTransactions> deposit(String accountId, BigDecimal amount);

  Mono<TransferResponse> transfer(String fromAccountId, String toAccountId, BigDecimal amount);

  Mono<BankAccount> saveSigner(String accountId, String signerId);

  Mono<BankAccount> deleteSigner(String accountId, String signerId);

  Mono<BankAccount> saveHolder(String accountId, String holderId);

  Mono<BankAccount> deleteHolder(String accountId, String holderId);

  Flux<AccountTransactions> findAccountTransactions(String accountId);
}
