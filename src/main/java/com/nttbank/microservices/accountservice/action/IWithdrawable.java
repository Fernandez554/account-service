package com.nttbank.microservices.accountservice.action;

import java.math.BigDecimal;

/**
 * A functional interface that defines a method for withdrawing money from a bank account.
 * Implementations of this interface should provide the logic to withdraw a specified amount
 * from the account.
 * This interface is intended for use in scenarios where withdrawing money from an account
 * is required.
 */
@FunctionalInterface
public interface IWithdrawable {

  void withdraw(BigDecimal amount);
}
