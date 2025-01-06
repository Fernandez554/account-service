package com.nttbank.microservices.accountservice.service;

import com.nttbank.microservices.accountservice.proxy.openfeign.CloudGatewayFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreditCardService {

  private final CloudGatewayFeign feignCreditCard;

  public Mono<Long> totalActiveCreditsCardsByCustomer(String customerId, String status) {
    return feignCreditCard.totalActiveCreditCardsByCustomer(customerId, status);
  }
}
