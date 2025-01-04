package com.nttbank.microservices.accountservice.proxy.openfeign;

import com.nttbank.microservices.accountservice.model.response.CustomerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Feign client interface for interacting with the external customer service. This client allows
 * making reactive HTTP requests to retrieve customer data.
 */
@ReactiveFeignClient(name = "cloud-gateway")
public interface CloudGatewayFeign {

  @GetMapping("/api/customer-service/customers")
  Flux<CustomerResponse> getAllCustomers();

  @GetMapping("/api/customer-service/customers/{customer_id}")
  Mono<CustomerResponse> findCustomerById(@PathVariable("customer_id") String customerId);

  @GetMapping("/api/creditcard-service/creditcards/{customer_id}/count")
  Mono<Long> totalActiveCreditsByCustomer(@PathVariable("customer_id") String customerId,
      @RequestParam("status") String status);

}
