package com.nttbank.microservices.accountservice.repo;

import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Repository interface for performing CRUD operations on {@link BankAccount} entities.
 * Extends {@link ReactiveMongoRepository} to provide reactive operations on MongoDB.
 * This interface also includes custom query methods for retrieving bank accounts based on
 * customer ID and account type.
 */
public interface IBankAccountRepo extends ReactiveMongoRepository<BankAccount, String> {
  Flux<BankAccount> findByCustomerIdAndAccountType(String customerId, String accountType);
}
