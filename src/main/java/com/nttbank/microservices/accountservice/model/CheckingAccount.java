package com.nttbank.microservices.accountservice.model;

import com.nttbank.microservices.accountservice.repo.IOpenable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@Data
public class CheckingAccount extends BankAccount implements IOpenable {

  private static final boolean OPEN_ACCOUNT_RESTRICTION_BY_PERSONAL = false;
  private static final int MAX_ACCOUNT_BY_PERSONAL = 1;

  private static final boolean OPEN_ACCOUNT_RESTRICTION_BY_BUSINESS = true;

  public CheckingAccount(BankAccount account) {
    this.setId(account.getId());
  }

  @Override
  public Mono<BankAccount> openAccount(BankAccount account) {
    return null;
  }
}
