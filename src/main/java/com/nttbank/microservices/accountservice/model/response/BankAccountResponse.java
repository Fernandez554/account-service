package com.nttbank.microservices.accountservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A generic response wrapper used to standardize responses for bank account operations.
 * This class encapsulates the status, message, and data for API responses.
 *
 * @param <T> the type of data that will be returned in the response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountResponse<T> {

  private int status;
  private String message;
  private T data;

}
