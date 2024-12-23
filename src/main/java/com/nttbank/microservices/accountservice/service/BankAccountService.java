package com.nttbank.microservices.accountservice.service;

import com.nttbank.microservices.accountservice.model.BankAccount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankAccountService {

  Mono<BankAccount> save(BankAccount t);

  Mono<BankAccount> update(BankAccount t);

  Flux<BankAccount> findAll();

  Mono<BankAccount> findById(String customerId);

  Mono<Void> delete(String customerId);

}
