package com.nttbank.microservices.accountservice.util;

import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.model.entity.MonthlyTransactionSummary;
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
import java.util.stream.Stream;

public class AccountUtils {

  private AccountUtils() {
  }

  public static final String CREDIT_CARD_STATUS_ACTIVE = "active";
  private static final Integer MAX_TRANS_PER_DAY_ACCOUNT_FIXED = 1;

  /**
   * Map that holds the key:value for  AccountType & Customer Profile if the pair is in the map an
   * additional validation will be triggered (check if the customer has an active credit card)
   **/
  public static final Map<String, Boolean> ACCOUNT_PROFILE_MAP =
      Map.of("saving:vip", true, "checking:pyme", true);

  public static final BiPredicate<String, String>
      CHECK_CUSTOMER_CREDIT_CARD = (accountType, profile) ->
      ACCOUNT_PROFILE_MAP.getOrDefault(accountType + ":" + profile, false);

  private static final IntPredicate isTheSameDay = dayToTest ->
      LocalDate.now().getDayOfMonth() == dayToTest;

  public static void isAbleToMakeTransactions(MonthlyTransactionSummary summary,
      Integer dayToTest, String accountId) {

    LocalDate today = LocalDate.now();
    Integer dayToValidate = Optional.ofNullable(dayToTest).orElseThrow(() ->
        new IllegalArgumentException(Constants.TRANSACTION_DAY_NOT_SET));

    MonthlyTransactionSummary dbSummary = Optional.ofNullable(summary)
        .orElse(MonthlyTransactionSummary.builder()
            .numberOfTransactions(BigDecimal.ZERO.intValue())
            .month(LocalDate.now().getMonthValue())
            .year(LocalDate.now().getYear())
            .build());

    boolean isMaxTransactionsExceeded = Stream.of(
        dbSummary.getMonth() == today.getMonthValue(),
        dbSummary.getYear() == today.getYear(),
        dbSummary.getNumberOfTransactions() >= MAX_TRANS_PER_DAY_ACCOUNT_FIXED
    ).allMatch(Boolean::booleanValue);

    if (!isTheSameDay.test(dayToValidate)) {
      throw new IllegalArgumentException(
          String.format(Constants.TRANSACTION_DAY_NOT_TODAY, dayToValidate));
    }

    if (isMaxTransactionsExceeded) {
      throw new IllegalArgumentException(
          String.format(Constants.MAX_TRANSACTION_LIMIT_EXCEEDED_ERROR, dayToValidate, accountId));
    }


  }

  public static final Map<String, Long> personalAccountLimit = Map.of("personal", Constants.ONE);
  public static final Map<String, Long> bothAccountLimits = Map.of(
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
