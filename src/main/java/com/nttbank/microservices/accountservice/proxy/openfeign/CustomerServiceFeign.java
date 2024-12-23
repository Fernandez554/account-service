package com.nttbank.microservices.accountservice.proxy.openfeign;

import com.nttbank.microservices.accountservice.model.Response.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

@FeignClient(name = "customer-service")
public interface CustomerServiceFeign {

  @GetMapping("/{customer_id}")
  Mono<ResponseEntity<CustomerResponse>> findById(@PathVariable("customer_id") String customerId);

}
