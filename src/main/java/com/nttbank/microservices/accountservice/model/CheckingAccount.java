package com.nttbank.microservices.accountservice.model;

import com.nttbank.microservices.accountservice.action.IDepositable;
import com.nttbank.microservices.accountservice.action.IOpenable;
import com.nttbank.microservices.accountservice.action.IWithdrawable;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.util.AccountUtils;
import com.nttbank.microservices.accountservice.util.Constants;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a checking account in the banking system. This class extends {@link BankAccount} and
 * implements {@link IOpenable}, {@link IWithdrawable}, and {@link IDepositable}. It allows
 * operations such as opening the account, depositing, and withdrawing funds.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class CheckingAccount extends BankAccount implements IOpenable, IWithdrawable, IDepositable {

  /**
   * Constructs a new CheckingAccount using the provided {@link BankAccount}.
   *
   * @param account the bank account to initialize the checking account from.
   */
  public CheckingAccount(BankAccount account) {
    super(account.getId(), account.getAccountType(), account.getCustomerId(), account.getBalance(),
        account.getMaxMonthlyTrans(), account.getMaintenanceFee(), account.getTransactionFee(),
        account.getAllowedDayOperation(), account.getWithdrawAmountMax(), account.getLstSigners(),
        account.getLstHolders(), account.getCreatedAt(), account.getUpdatedAt(),
        account.getMonthlyTransactionSummary(), account.getLstTransactions());
  }

  @Override
  public void openAccount(Long numAccounts, String customerType) {
    if (null == this.getMaintenanceFee()) {
      throw new IllegalArgumentException(
          Constants.MAINTENANCE_FEE_REQUIRED);
    }
    AccountUtils.defaultOpenAccountValidationMethod(numAccounts, customerType,
        AccountUtils.personalAccountLimit, this.getAccountType());
  }

  @Override
  public void withdraw(BigDecimal amount) {
    this.setBalance(AccountUtils.defaultWithdrawMethod(this.getBalance(), amount, this.getId()));
  }

  @Override
  public void deposit(BigDecimal amount) {
    this.setBalance(AccountUtils.defaultDepositMethod(this.getBalance(), amount));
  }
}

