package com.nttbank.microservices.accountservice.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Utility class for creating various types of responses for HTTP requests.
 * This class provides static methods for creating {@link ResponseEntity} objects with
 * different HTTP status codes, including success, error, and not found responses.
 *
 * <p>By using these utility methods, you can easily create and return common response types
 * in a reactive programming environment with {@link Mono}.</p>
 */
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
