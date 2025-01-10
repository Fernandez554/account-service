package com.nttbank.microservices.accountservice.util;

import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntPredicate;

public class AccountUtils {

  private AccountUtils() {
  }

  public static final String CREDIT_CARD_STATUS_ACTIVE = "active";

  public static final Map<String, Boolean> ACCOUNT_PROFILE_MAP =
      Map.of("saving:vip", true, "checking:pyme", true);

  public static final BiPredicate<String, String> CHECK_CUSTOMER_CREDIT_CARD =
      (accountType, profile) ->
          ACCOUNT_PROFILE_MAP.getOrDefault(accountType + ":" + profile, false);

  public static IntPredicate isAbleToMakeTransactions = dayToTest -> {
    int todayDay = LocalDate.now().getDayOfMonth();
    return dayToTest == todayDay;
  };

  public static void validateTransactionDay(Integer dayToTest) {
    if (dayToTest == null) {
      throw new IllegalArgumentException(Constants.TRANSACTION_DAY_NOT_SET);
    }
    if (!isAbleToMakeTransactions.test(dayToTest)) {
      throw new IllegalArgumentException(
          String.format(Constants.TRANSACTION_DAY_NOT_TODAY, dayToTest)
      );
    }
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

  public static <T> void addElementToSet(BankAccount account, T element,
      Function<BankAccount, Set<T>> getter, BiConsumer<BankAccount, Set<T>> setter) {
    Set<T> set = Optional.ofNullable(getter.apply(account))
        .orElseGet(() -> {
          Set<T> newSet = new HashSet<>();
          setter.accept(account, newSet);
          return newSet;
        });
    set.add(element);
    setter.accept(account, set);
  }

  public static <T> void removeElementToSet(BankAccount account, T element,
      Function<BankAccount, Set<T>> getter, BiConsumer<BankAccount, Set<T>> setter) {
    Set<T> set = Optional.ofNullable(getter.apply(account))
        .orElseGet(() -> {
          Set<T> newSet = new HashSet<>();
          setter.accept(account, newSet);
          return newSet;
        });
    set.remove(element);
    setter.accept(account, set);
  }


}
