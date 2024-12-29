package com.nttbank.microservices.accountservice.action;

import java.math.BigDecimal;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface IConsultable {
  Mono<BigDecimal> getBalance(String accountId);
}
