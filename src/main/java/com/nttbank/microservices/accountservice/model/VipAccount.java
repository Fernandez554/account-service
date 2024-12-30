package com.nttbank.microservices.accountservice.model;

import com.nttbank.microservices.accountservice.action.IOpenable;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a VIP account, which extends the basic functionality of a {@link BankAccount}.
 * This account type is associated with specific business rules, such as account opening conditions.
 * It implements the {@link IOpenable} interface to define the rules for opening a VIP account.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class VipAccount extends BankAccount implements IOpenable {

  /**
   * Constructs a VIP account from an existing {@link BankAccount}.
   *
   * @param account the {@link BankAccount} from which the VIP account details are copied
   */
  public VipAccount(BankAccount account) {
    super(account.getId(), account.getAccountType(), account.getCustomerId(), account.getBalance(),
        account.getMaxMonthlyTrans(), account.getMaintenanceFee(),
        account.getAllowedWithdrawalDay(), account.getWithdrawAmountMax(), account.getLstSigners(),
        account.getLstHolders(), account.getCreationDate());
  }

  @Override
  public boolean openAccount(Long numAccounts, String customerType) {
    //TODO: Validar si tiene tarjeta de credito
    return false;
  }
}
