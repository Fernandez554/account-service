package com.nttbank.microservices.accountservice.repo;

import com.nttbank.microservices.accountservice.model.BankAccount;
import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface IWithdrawable {
  Mono<BankAccount> withdraw(String accountId, BigDecimal amount);
}
