package com.nttbank.microservices.accountservice.model;

import com.nttbank.microservices.accountservice.action.IOpenable;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

@Data
@NoArgsConstructor
public class FixedDepositAccount extends BankAccount implements IOpenable {

  @Transient
  public final long MAX_ACCOUNTS_BY_PERSONAL = 1;
  @Transient
  public final long MAX_ACCOUNTS_BY_BUSINESS = 0;

  public FixedDepositAccount(BankAccount account) {
    super(account.getId(), account.getAccountType(), account.getCustomerId(), account.getBalance(),
        account.getMaxMonthlyTrans(), account.getMaintenanceFee(),
        account.getAllowedWithdrawalDay(), account.getWithdrawAmountMax(), account.getLstSigners(),
        account.getLstHolders());
  }

  @Override
  public boolean openAccount(Long numAccounts, String customerType) {
    return ("personal".equals(customerType) && numAccounts < MAX_ACCOUNTS_BY_PERSONAL) || (
        "business".equals(customerType) && numAccounts < MAX_ACCOUNTS_BY_BUSINESS);

  }

}
