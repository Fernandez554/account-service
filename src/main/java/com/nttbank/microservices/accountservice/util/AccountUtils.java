package com.nttbank.microservices.accountservice.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class AccountUtils {

  private AccountUtils() {
  }

  public static Predicate<Integer> isAbleToMakeTransactions = dayToTest -> {
    if (dayToTest == null) {
      throw new IllegalArgumentException(Constants.TRANSACTION_DAY_NOT_SET);
    }
    try {
      int todayDay = LocalDate.now().getDayOfMonth();
      if (dayToTest != todayDay) {
        throw new IllegalArgumentException(
            String.format(Constants.TRANSACTION_DAY_NOT_TODAY, dayToTest)
        );
      }
      return true;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          String.format(Constants.TRANSACTION_DAY_NOT_TODAY, dayToTest)
      );
    }
  };

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
