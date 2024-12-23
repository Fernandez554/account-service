package com.nttbank.microservices.accountservice.repo;

import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface IConsultable {
  Mono<BigDecimal> getBalance(String accountId);
}
