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
 * Fixed Deposit Account class, extending {@link BankAccount}, and implementing {@link IOpenable}
 * and {@link IWithdrawable}. This account type has restrictions based on customer type (personal or
 * business) and a fixed deposit nature. It also provides functionality for withdrawing from the
 * account.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class FixedDepositAccount extends BankAccount implements IOpenable, IWithdrawable,
    IDepositable {

  /**
   * Constructs a new {@link FixedDepositAccount} with the specified base {@link BankAccount}
   * details.
   *
   * @param account the base {@link BankAccount} from which details are copied.
   */
  public FixedDepositAccount(BankAccount account) {
    super(account.getId(), account.getAccountType(), account.getCustomerId(), account.getBalance(),
        account.getMaxMonthlyTrans(), account.getMaintenanceFee(), account.getTransactionFee(),
        account.getAllowedDayOperation(), account.getWithdrawAmountMax(), account.getLstSigners(),
        account.getLstHolders(), account.getCreatedAt(), account.getUpdatedAt(),
        account.getMonthlyTransactionSummary(), account.getLstTransactions());
  }

  @Override
  public void openAccount(Long numAccounts, String customerType) {
    if (null == this.getAllowedDayOperation()) {
      throw new IllegalArgumentException(
          Constants.ALLOWED_DAY_OP_REQUIRED);
    }
    AccountUtils.defaultOpenAccountValidationMethod(numAccounts, customerType,
        AccountUtils.businessAccountLimits, this.getAccountType());
  }

  @Override
  public void withdraw(BigDecimal amount) {
    AccountUtils.validateTransactionDay(this.getAllowedDayOperation());
    this.setBalance(AccountUtils.defaultWithdrawMethod(this.getBalance(), amount, this.getId()));
  }

  @Override
  public void deposit(BigDecimal amount) {
    AccountUtils.validateTransactionDay(this.getAllowedDayOperation());
    this.setBalance(AccountUtils.defaultDepositMethod(this.getBalance(), amount));
  }

}
