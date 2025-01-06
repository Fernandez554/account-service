package com.nttbank.microservices.accountservice.service;

import com.nttbank.microservices.accountservice.model.response.MovementResponse;
import com.nttbank.microservices.accountservice.proxy.openfeign.CloudGatewayFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service class responsible for handling movement-related operations.
 */
@Service
@RequiredArgsConstructor
public class MovementService {

  private final CloudGatewayFeign feignCustomer;

  public Mono<MovementResponse> saveMovement(MovementResponse movement) {
    return feignCustomer.saveMovement(movement);
  }
}
