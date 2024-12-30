package com.nttbank.microservices.accountservice.model;

import com.nttbank.microservices.accountservice.action.IDepositable;
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
 * Represents a checking account in the banking system. This class extends {@link BankAccount} and
 * implements {@link IOpenable}, {@link IWithdrawable}, and {@link IDepositable}. It allows
 * operations such as opening the account, depositing, and withdrawing funds.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class CheckingAccount extends BankAccount implements IOpenable, IWithdrawable, IDepositable {

  @Transient
  public final long maxAccountsByPersonal = 1;

  /**
   * Constructs a new CheckingAccount using the provided {@link BankAccount}.
   *
   * @param account the bank account to initialize the checking account from.
   */
  public CheckingAccount(BankAccount account) {
    super(account.getId(), account.getAccountType(), account.getCustomerId(), account.getBalance(),
        account.getMaxMonthlyTrans(), account.getMaintenanceFee(),
        account.getAllowedWithdrawalDay(), account.getWithdrawAmountMax(), account.getLstSigners(),
        account.getLstHolders(), account.getCreationDate());
  }


  @Override
  public boolean openAccount(Long numAccounts, String customerType) {
    //TODO: Validar que comision por mantenimiento no sea 0.
    return !"personal".equals(customerType) || numAccounts < maxAccountsByPersonal;
  }

  @Override
  public void withdraw(BigDecimal amount) {
    BigDecimal actualBalance = (this.getBalance() == null ? BigDecimal.ZERO : this.getBalance());
    actualBalance = actualBalance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
    if (actualBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException(
          "Account balance " + this.getId()
              + " doesn't have the enough funds to cover the withdraw");
    } else {
      this.setBalance(actualBalance);
    }
  }

  @Override
  public void deposit(BigDecimal amount) {
    BigDecimal actualBalance = (this.getBalance() == null ? BigDecimal.ZERO : this.getBalance());
    this.setBalance(actualBalance.add(amount).setScale(2, RoundingMode.HALF_UP));
  }
}

