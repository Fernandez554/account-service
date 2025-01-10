package com.nttbank.microservices.accountservice.repo;

import com.nttbank.microservices.accountservice.model.entity.AccountTransactions;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/** Repository interface for performing CRUD operations on {@link AccountTransactions} entities.
 * Extends {@link ReactiveMongoRepository} to provide reactive operations on MongoDB. */
public interface IAccountTransactionRepo extends
    ReactiveMongoRepository<AccountTransactions, String> {
  Flux<AccountTransactions> findAllByAccountId(String accountId);

  Flux<AccountTransactions> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);

  Flux<AccountTransactions> findByProductNameAndTypeAndCreatedAtBetween(
      String productName,
      String type,
      LocalDateTime startDate,
      LocalDateTime endDate
  );

}
