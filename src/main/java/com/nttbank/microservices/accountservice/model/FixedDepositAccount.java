package com.nttbank.microservices.accountservice.model;

import com.nttbank.microservices.accountservice.action.IOpenable;
import com.nttbank.microservices.accountservice.action.IWithdrawable;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

/**
 * Fixed Deposit Account class, extending {@link BankAccount}, and implementing {@link IOpenable}
 * and {@link IWithdrawable}.
 * This account type has restrictions based on customer type (personal or business)
 * and a fixed deposit nature. It also provides functionality for withdrawing from the account.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class FixedDepositAccount extends BankAccount implements IOpenable, IWithdrawable {

  @Transient
  public final long maxAccountsByPersonal = 1;
  @Transient
  public final long maxAccountsByBusiness = 0;

  /**
   * Constructs a new {@link FixedDepositAccount} with the specified base
   * {@link BankAccount} details.
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
  public boolean openAccount(Long numAccounts, String customerType) {
    return ("personal".equals(customerType) && numAccounts < maxAccountsByPersonal) || (
        "business".equals(customerType) && numAccounts < maxAccountsByBusiness);

  }

  @Override
  public void withdraw(BigDecimal amount) {
    BigDecimal actualBalance = (this.getBalance() == null ? BigDecimal.ZERO : this.getBalance());
    actualBalance = actualBalance.subtract(amount).setScale(2, RoundingMode.HALF_DOWN);
    if (actualBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException(
          "Account balance " + this.getId()
              + " doesn't have the enough funds to cover the withdraw");
    } else {
      this.setBalance(actualBalance);
    }
  }

}
