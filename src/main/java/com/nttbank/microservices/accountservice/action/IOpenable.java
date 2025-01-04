package com.nttbank.microservices.accountservice.action;

/**
 * A functional interface that defines a method for opening a bank account. Implementations of this
 * interface should provide the logic for determining if a bank account can be opened based on the
 * number of accounts the customer currently has and the customer's type. This interface is intended
 * for use in scenarios where the ability to open a bank account is required.
 */
@FunctionalInterface
public interface IOpenable {

  void openAccount(Long numAccounts, String customerType);
}
