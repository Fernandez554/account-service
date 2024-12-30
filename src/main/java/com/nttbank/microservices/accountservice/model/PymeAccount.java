package com.nttbank.microservices.accountservice.model;

import com.nttbank.microservices.accountservice.action.IWithdrawable;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a Pyme (Small and Medium-sized Enterprises) Bank Account.
 * This class extends the {@link BankAccount} and can hold specific attributes
 * for a Pyme account, if needed. Currently, it inherits all the properties
 * from the {@link BankAccount} class.
 *
 * <p>The {@code PymeAccount} class does not add any additional fields or behavior
 * to the {@link BankAccount} class, but it can be extended in the future to include
 * specific logic or properties for Pyme accounts.</p>
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class PymeAccount extends BankAccount {

  /**
   * Constructs a new {@code PymeAccount} from an existing {@link BankAccount}.
   * This constructor allows for the conversion of a generic {@link BankAccount}
   * into a {@code PymeAccount}, copying all properties from the original account.
   *
   * @param account the {@link BankAccount} to copy properties from
   */
  public PymeAccount(BankAccount account) {
    super(account.getId(), account.getAccountType(), account.getCustomerId(), account.getBalance(),
        account.getMaxMonthlyTrans(), account.getMaintenanceFee(),
        account.getAllowedWithdrawalDay(), account.getWithdrawAmountMax(), account.getLstSigners(),
        account.getLstHolders(), account.getCreationDate());
  }


}
