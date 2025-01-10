package com.nttbank.microservices.accountservice.repo;

import com.nttbank.microservices.accountservice.model.entity.AccountTransactions;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/** Repository interface for performing CRUD operations on {@link AccountTransactions} entities.
 * Extends {@link ReactiveMongoRepository} to provide reactive operations on MongoDB. */
public interface IAccountTransactionRepo extends
    ReactiveMongoRepository<AccountTransactions, String> {
  Flux<AccountTransactions> findAllByAccountId(String accountId);
}
