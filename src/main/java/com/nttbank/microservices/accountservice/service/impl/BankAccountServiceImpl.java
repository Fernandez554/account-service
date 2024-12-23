package com.nttbank.microservices.accountservice.service.impl;

import com.nttbank.microservices.accountservice.model.BankAccount;
import com.nttbank.microservices.accountservice.repo.IBankAccountRepo;
import com.nttbank.microservices.accountservice.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

  private final IBankAccountRepo repo;

  @Override
  public Mono<BankAccount> save(BankAccount t) {
    return repo.save(t);
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
  public Mono<BankAccount> findById(String customerId) {
    return repo.findById(customerId);
  }

  @Override
  public Mono<Void> delete(String customerId) {
    return repo.deleteById(customerId);
  }
}
