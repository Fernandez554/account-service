package com.nttbank.microservices.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

/**
 * The entry point of the Account Service application.
 *
 * <p>
 * This application is a Spring Boot application and enables reactive Feign clients for making
 * asynchronous HTTP requests.
 * </p>
 *
 * <p>
 * Annotations:
 * <ul>
 *   <li>{@link org.springframework.boot.autoconfigure.SpringBootApplication}
 *   - Marks this as a Spring Boot application.</li>
 *   <li>{@link reactivefeign.spring.config.EnableReactiveFeignClients}
 *   - Enables the use of reactive Feign clients.</li>
 * </ul>
 */
@SpringBootApplication
@EnableReactiveFeignClients
public class AccountServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccountServiceApplication.class, args);
  }

}