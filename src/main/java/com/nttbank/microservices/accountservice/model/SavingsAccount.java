package com.nttbank.microservices.accountservice.model;

import com.nttbank.microservices.accountservice.action.IDepositable;
import com.nttbank.microservices.accountservice.action.IOpenable;
import com.nttbank.microservices.accountservice.action.IWithdrawable;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.util.AccountUtils;
import com.nttbank.microservices.accountservice.util.Constants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a saving account in the banking system. This class extends {@link BankAccount} and
 * implements {@link IOpenable}, {@link IWithdrawable}. It allows operations such as opening the
 * account and withdrawing funds.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class SavingsAccount extends BankAccount implements IOpenable, IWithdrawable, IDepositable {

  /**
   * Constructor that creates a SavingsAccount from an existing BankAccount object.
   *
   * @param account The BankAccount object to convert into a SavingsAccount.
   */
  public SavingsAccount(BankAccount account) {
    super(account.getId(), account.getAccountType(), account.getCustomerId(), account.getBalance(),
        account.getMaxMonthlyTrans(), account.getMaintenanceFee(),
        account.getAllowedWithdrawalDay(), account.getWithdrawAmountMax(), account.getLstSigners(),
        account.getLstHolders(), account.getCreationDate());
  }

  @Override
  public void openAccount(Long numAccounts, String customerType) {
    //TODO: validar que tenga seteado MaxMonthlyTrans
    AccountUtils.defaultOpenAccountValidationMethod(numAccounts, customerType,
        AccountUtils.businessAccountLimits, this.getAccountType());
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
