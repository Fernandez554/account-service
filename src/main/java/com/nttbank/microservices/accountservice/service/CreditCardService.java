package com.nttbank.microservices.accountservice.service;

import com.nttbank.microservices.accountservice.proxy.openfeign.CloudGatewayFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditCardService {

  private final CloudGatewayFeign feignCreditCard;

  public Mono<Long> totalActiveCreditsCardsByCustomer(String customerId, String status) {
    return feignCreditCard.totalActiveCreditCardsByCustomer(customerId, status)
        .onErrorResume(e -> {
          log.error("Error retrieving credit cards info from customer: {}", e.getMessage());
          return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "Error retrieving credit cards info from customer :" + e.getMessage(), e));
        });
  }
}
