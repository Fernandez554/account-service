package com.nttbank.microservices.accountservice.service;

import com.nttbank.microservices.accountservice.model.response.CustomerResponse;
import com.nttbank.microservices.accountservice.proxy.openfeign.CloudGatewayFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class responsible for handling customer-related operations. This service interacts with a
 * Feign client to retrieve customer data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CloudGatewayFeign feignCustomer;

  public Flux<CustomerResponse> getAllCustomers() {
    return feignCustomer.getAllCustomers();
  }

  public Mono<CustomerResponse> findCustomerById(String customerId) {
    return feignCustomer.findCustomerById(customerId)
        .onErrorResume(e -> {
          log.error("Error retrieving customer data: {}", e.getMessage());
          return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "Error retrieving customer data: " + e.getMessage(), e));
        });
  }
}
