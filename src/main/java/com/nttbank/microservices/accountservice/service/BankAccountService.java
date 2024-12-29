package com.nttbank.microservices.accountservice.service;

import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankAccountService {

  Mono<BankAccount> save(BankAccount t, ServerHttpRequest req);

  Mono<BankAccount> update(BankAccount t);

  Flux<BankAccount> findAll();

  Mono<BankAccount> findById(String customerId);

  Mono<Void> delete(String customerId);

  //Flux<BankAccount> getAccountsByCustomerIdAndType(String customerId, String accountType);
}
