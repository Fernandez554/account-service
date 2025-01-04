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
 * Fixed Deposit Account class, extending {@link BankAccount}, and implementing {@link IOpenable}
 * and {@link IWithdrawable}. This account type has restrictions based on customer type (personal or
 * business) and a fixed deposit nature. It also provides functionality for withdrawing from the
 * account.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class FixedDepositAccount extends BankAccount implements IOpenable, IWithdrawable {

  /**
   * Constructs a new {@link FixedDepositAccount} with the specified base {@link BankAccount}
   * details.
   *
   * @param account the base {@link BankAccount} from which details are copied.
   */
  public FixedDepositAccount(BankAccount account) {
    super(account.getId(), account.getAccountType(), account.getCustomerId(), account.getBalance(),
        account.getMaxMonthlyTrans(), account.getMaintenanceFee(),
        account.getAllowedWithdrawalDay(), account.getWithdrawAmountMax(), account.getLstSigners(),
        account.getLstHolders(), account.getCreationDate());
  }

  @Override
  public void openAccount(Long numAccounts, String customerType) {
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
