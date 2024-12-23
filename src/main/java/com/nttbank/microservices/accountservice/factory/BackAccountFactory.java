package com.nttbank.microservices.accountservice.factory;

import com.nttbank.microservices.accountservice.model.BankAccount;
import com.nttbank.microservices.accountservice.model.CheckingAccount;
import com.nttbank.microservices.accountservice.model.FixedDepositAccount;
import com.nttbank.microservices.accountservice.model.SavingsAccount;

public class BackAccountFactory {

//  public static BankAccount createAccount(BankAccount account) {
//    return switch (account.getAccountType()) {
//      case "saving" -> new SavingsAccount(account);
//      case "checking" -> new CheckingAccount();
//      case "fixed" -> new FixedDepositAccount();
//      default -> throw new IllegalArgumentException("Invalid account type");
//    };
//  }
}
