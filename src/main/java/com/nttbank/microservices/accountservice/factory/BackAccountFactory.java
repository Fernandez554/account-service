package com.nttbank.microservices.accountservice.factory;

import com.nttbank.microservices.accountservice.model.CheckingAccount;
import com.nttbank.microservices.accountservice.model.FixedDepositAccount;
import com.nttbank.microservices.accountservice.model.SavingsAccount;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Factory class for creating different types of bank accounts.
 *
 * <p>
 * This class uses a map of account types to functions that instantiate the corresponding account
 * types. It provides a centralized way to create bank accounts based on their type.
 * </p>
 */
public class BackAccountFactory {

  private BackAccountFactory() {
  }

  /**
   * A map of account types to functions that create the respective account instances.
   */
  private static final Map<String, UnaryOperator<BankAccount>> accountCreators = Map.of(
      "saving", SavingsAccount::new,
      "checking", CheckingAccount::new,
      "fixed", FixedDepositAccount::new
  );

  /**
   * Creates a new bank account of the specified type.
   *
   * @param type            the type of account to create (e.g., "saving", "checking").
   * @param existingAccount an existing account instance to initialize the new account.
   * @return a new instance of the specified bank account type.
   * @throws IllegalArgumentException if the specified type is not recognized.
   */
  public static BankAccount createAccount(String type, BankAccount existingAccount) {
    UnaryOperator<BankAccount> creator = accountCreators.get(type.toLowerCase());
    if (creator == null) {
      throw new IllegalArgumentException("Unknown account type: " + type);
    }
    return creator.apply(existingAccount);
  }
}
