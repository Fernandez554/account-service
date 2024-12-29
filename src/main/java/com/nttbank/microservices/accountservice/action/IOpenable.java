package com.nttbank.microservices.accountservice.action;

public interface IOpenable {
  boolean openAccount(Long numAccounts, String customerType);
}
