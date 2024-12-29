package com.nttbank.microservices.accountservice.proxy.openfeign;

import com.nttbank.microservices.accountservice.model.Response.CustomerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "cloud-gateway")
public interface FeignClientCustomer {

  @GetMapping("/api/customer-service/customers")
  Flux<CustomerResponse> getAllCustomers();

  @GetMapping("/api/customer-service/customers/{customer_id}")
  Mono<CustomerResponse> findCustomerById(@PathVariable("customer_id") String customerId);

}