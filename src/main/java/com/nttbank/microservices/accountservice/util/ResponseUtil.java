package com.nttbank.microservices.accountservice.util;

import com.nttbank.microservices.accountservice.model.Response.BankAccountResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ResponseUtil {
  // General error response creator with status and type information
  public static <T> Mono<ResponseEntity<T>> createErrorResponse(HttpStatus status, Class<T> clazz) {
    return Mono.just(ResponseEntity.status(status).body(null));
  }

  public static <T> Mono<ResponseEntity<T>> createdResponse(T t) {
    return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(t));
  }

  public static <T> Mono<ResponseEntity<T>> notFoundResponse(T t) {
    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(t));
  }

  public static <T> Mono<ResponseEntity<T>> badRequestResponse(T t) {
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(t));

  }

  public static <T> Mono<ResponseEntity<T>> forbiddenRequestResponse(T t) {
    return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(t));
  }

  public static <T> Mono<ResponseEntity<T>> createNotFoundResponse(Class<T> clazz) {
    return createErrorResponse(HttpStatus.NOT_FOUND, clazz);
  }

  public static <T> ResponseEntity<T> successResponse(T body) {
    return ResponseEntity.ok(body);
  }

}
