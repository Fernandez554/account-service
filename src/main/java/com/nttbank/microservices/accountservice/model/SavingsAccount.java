package com.nttbank.microservices.accountservice.model;

import com.nttbank.microservices.accountservice.action.IOpenable;
import com.nttbank.microservices.accountservice.action.IWithdrawable;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
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
public class SavingsAccount extends BankAccount implements IOpenable, IWithdrawable {

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
    Map<String, Long> accountLimits = Map.of(
        "personal", Constants.ONE,
        "business", Constants.ZERO
    );

    // Validate the customer type and the number of accounts
    Optional.ofNullable(accountLimits.get(customerType))
        .filter(limit -> numAccounts < limit)
        .orElseThrow(() -> new IllegalArgumentException(
            String.format(Constants.OPENING_ACCOUNT_RESTRICTION, this.getAccountType())));
  }

  @Override
  public void withdraw(BigDecimal amount) {
    BigDecimal actualBalance = (this.getBalance() == null ? BigDecimal.ZERO : this.getBalance());
    actualBalance = actualBalance.subtract(amount).setScale(2, RoundingMode.HALF_DOWN);
    if (actualBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException(
          String.format(Constants.NO_WITHDRAW_FUNDS_AVAILABLE, this.getId()));
    } else {
      this.setBalance(actualBalance);
    }
  }
}
