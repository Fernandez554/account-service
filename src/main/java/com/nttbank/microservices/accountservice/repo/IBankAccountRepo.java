package com.nttbank.microservices.accountservice.repo;

import com.nttbank.microservices.accountservice.model.BankAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IBankAccountRepo extends ReactiveMongoRepository<BankAccount, String> { }
