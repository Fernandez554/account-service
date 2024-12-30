package com.nttbank.microservices.accountservice.action;

import java.math.BigDecimal;

/**
 * A functional interface that defines a method for depositing money into a bank account.
 * Implementations of this interface should provide the logic to deposit a specified
 * amount into an account.
 * This interface is intended for use in scenarios where depositing money into an
 * account is required.
 */
@FunctionalInterface
public interface IDepositable {
  void deposit(BigDecimal amount);
}
