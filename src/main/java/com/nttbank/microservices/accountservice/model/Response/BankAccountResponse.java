package com.nttbank.microservices.accountservice.model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountResponse<T> {

  private int status;
  private String message;
  private T data;

}
