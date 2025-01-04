package com.nttbank.microservices.accountservice.action;

/**
 * A functional interface that defines a method for consulting a bank account's balance.
 * Implementations of this interface should provide the logic to retrieve the balance of a specified
 * account. This interface is intended to be used in scenarios where checking the balance is
 * required.
 */
@FunctionalInterface
public interface IConsultable {

  void getBalance(String accountId);
}
