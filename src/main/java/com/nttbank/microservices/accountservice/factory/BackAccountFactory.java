package com.nttbank.microservices.accountservice.factory;

import com.nttbank.microservices.accountservice.model.CheckingAccount;
import com.nttbank.microservices.accountservice.model.FixedDepositAccount;
import com.nttbank.microservices.accountservice.model.PymeAccount;
import com.nttbank.microservices.accountservice.model.SavingsAccount;
import com.nttbank.microservices.accountservice.model.VipAccount;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import java.util.Map;
import java.util.function.Function;

public class BackAccountFactory {

  private static final Map<String, Function<BankAccount, BankAccount>> accountCreators = Map.of(
      "saving", SavingsAccount::new,
      "checking", CheckingAccount::new,
      "fixed", FixedDepositAccount::new,
      "vip", VipAccount::new,
      "pyme", PymeAccount::new

  );

  public static BankAccount createAccount(String type, BankAccount existingAccount) {
    Function<BankAccount, BankAccount> creator = accountCreators.get(type.toLowerCase());
    if (creator == null) {
      throw new IllegalArgumentException("Unknown account type: " + type);
    }
    return creator.apply(existingAccount);
  }
}
