package com.nttbank.microservices.accountservice.model;

import com.nttbank.microservices.accountservice.action.IDepositable;
import com.nttbank.microservices.accountservice.action.IOpenable;
import com.nttbank.microservices.accountservice.action.IWithdrawable;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

/**
 * Represents a saving account in the banking system. This class extends {@link BankAccount} and
 * implements {@link IOpenable}, {@link IWithdrawable}. It allows
 * operations such as opening the account and withdrawing funds.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class SavingsAccount extends BankAccount implements IOpenable, IWithdrawable {

  @Transient
  public final long maxAccountsByPersonal = 1;
  @Transient
  public final long maxAccountsByBusiness = 0;

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
  public boolean openAccount(Long numAccounts, String customerType) {
    //TODO: validar que tenga seteado MaxMonthlyTrans
    return ("personal".equals(customerType) && numAccounts < maxAccountsByPersonal) || (
        "business".equals(customerType) && numAccounts < maxAccountsByBusiness);
  }

  @Override
  public void withdraw(BigDecimal amount) {
    BigDecimal actualBalance = (this.getBalance() == null ? BigDecimal.ZERO : this.getBalance());
    actualBalance = actualBalance.subtract(amount).setScale(2, RoundingMode.HALF_DOWN);
    if (actualBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Account balance " + this.getId()
          + " doesn't have the enough funds to cover the withdraw");
    } else {
      this.setBalance(actualBalance);
    }
  }
}
