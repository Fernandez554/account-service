package com.nttbank.microservices.accountservice.service;

import com.nttbank.microservices.accountservice.model.Response.CustomerResponse;
import com.nttbank.microservices.accountservice.proxy.openfeign.FeignClientCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final FeignClientCustomer feignCustomer;

  public Flux<CustomerResponse> getAllCustomers() {
    return feignCustomer.getAllCustomers();
  }

  public Mono<CustomerResponse> findCustomerById(String customerId) {
    return feignCustomer.findCustomerById(customerId);
  }
}
