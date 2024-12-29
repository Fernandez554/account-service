package com.nttbank.microservices.accountservice.repo;

import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface IBankAccountRepo extends ReactiveMongoRepository<BankAccount, String> {
  Flux<BankAccount> findByCustomerIdAndAccountType(String customerId, String accountType);
}
