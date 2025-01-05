package com.nttbank.microservices.accountservice.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

public class AccountUtils {

  private AccountUtils() {
  }

  public static final Map<String, Long> personalAccountLimit = Map.of("personal", Constants.ONE);
  public static final Map<String, Long> businessAccountLimits = Map.of(
      "personal", Constants.ONE,
      "business", Constants.ZERO
  );

  public static void defaultOpenAccountValidationMethod(Long numAccounts, String customerType,
      Map<String, Long> accountLimits, String accountType) {
    Optional.ofNullable(accountLimits.get(customerType))
        .filter(limit -> numAccounts < limit)
        .orElseThrow(() -> new IllegalArgumentException(
            String.format(Constants.OPENING_ACCOUNT_RESTRICTION, accountType)));
  }

  public static BigDecimal defaultWithdrawMethod(BigDecimal balance, BigDecimal amount,
      String accountId) {
    BigDecimal actualBalance = (balance == null ? BigDecimal.ZERO : balance);
    actualBalance = actualBalance.subtract(amount).setScale(2, RoundingMode.HALF_UP);

    if (actualBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException(
          String.format(Constants.NO_WITHDRAW_FUNDS_AVAILABLE, accountId));
    }
    return actualBalance;
  }

  public static BigDecimal defaultDepositMethod(BigDecimal balance, BigDecimal amount) {
    BigDecimal actualBalance = (balance == null ? BigDecimal.ZERO : balance);
    return actualBalance.add(amount).setScale(2, RoundingMode.HALF_UP);
  }
}
