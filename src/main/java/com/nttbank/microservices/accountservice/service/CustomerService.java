package com.nttbank.microservices.accountservice.service;

import com.nttbank.microservices.accountservice.model.Response.CustomerResponse;
import com.nttbank.microservices.accountservice.proxy.openfeign.CustomerServiceFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerServiceFeign customerServiceFeign;

  public Mono<CustomerResponse> findCustomerById(String customerId) {
    return customerServiceFeign.findById(customerId)
        .filter(response -> response.getStatusCode().is2xxSuccessful())
        .map(ResponseEntity::getBody);
  }
}
