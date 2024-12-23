package com.nttbank.microservices.accountservice.model;

public class FixedDepositAccount extends BankAccount {
  private static final boolean OPEN_ACCOUNT_RESTRICTION_BY_PERSONAL = false;
  private static final int MAX_ACCOUNT_BY_PERSONAL = 1;

  private static final boolean OPEN_ACCOUNT_RESTRICTION_BY_BUSINESS = false;
  private static final int MAX_ACCOUNT_BY_BUSINESS = 0;

  public FixedDepositAccount(BankAccount account) {
    this.setId(account.getId());
  }

}
