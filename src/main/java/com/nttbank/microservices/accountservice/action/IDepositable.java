package com.nttbank.microservices.accountservice.action;

import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface IDepositable {
  Mono<BankAccount> deposit(String accountId, BigDecimal amount);
}
