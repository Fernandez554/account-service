package com.nttbank.microservices.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
public class SavingsAccount extends BankAccount {

  private static final boolean OPEN_ACCOUNT_RESTRICTION_BY_PERSONAL = false;
  private static final int MAX_ACCOUNT_BY_PERSONAL = 1;

  private static final boolean OPEN_ACCOUNT_RESTRICTION_BY_BUSINESS = true;

  public SavingsAccount(BankAccount account) {
    this.setId(account.getId());
  }

}
