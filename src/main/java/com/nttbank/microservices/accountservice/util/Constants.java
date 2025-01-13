package com.nttbank.microservices.accountservice.util;

public class Constants {

  private Constants() {
  }

  public static final String FEES = "fee";
  public static final String STATUS_KEY = "status";
  public static final String MESSAGE_KEY = "message";
  public static final String PATH_KEY = "path";
  public static final String ERROR_KEY = "error";
  public static final String WITHDRAWAL = "withdrawal";
  public static final String DEPOSIT = "deposit";
  public static final String PAYMENT = "payment";
  public static final String CHARGE = "charge";
  public static final String TRANSACTION_DAY_NOT_SET =
      "The transaction cannot be processed because the specific "
          + "day for transactions has not been set for this account.";
  public static final String TRANSACTION_DAY_NOT_TODAY = "This account allows transactions only "
      + "on %sth day of the month.";
  public static final String MAX_TRANSACTION_LIMIT_EXCEEDED_ERROR = "Transaction limit for the day "
      + "has been exceeded. You cannot perform more than %d transactions today for this account %s.";
  public static final String INVALID_ACCOUNT_TYPE =
      "Invalid account type. ";
  public static final String NO_ACTIVE_CREDIT_CARDS =
      "Customer must have at least one active credit card to open this account";
  public static final String OPENING_ACCOUNT_RESTRICTION =
      "Customer is restricted from opening a new account of type %s";
  public static final String NO_WITHDRAW_FUNDS_AVAILABLE =
      "Sender Account doesn't have the enough funds to cover the operation";
  public static final String MAX_MONTHLY_TRANS_REQUIRED =
      "Max monthly transactions must be set for this account type";
  public static final String MAINTENANCE_FEE_REQUIRED =
      "Maintenance fee must be set for this account type";
  public static final String ALLOWED_DAY_OP_REQUIRED =
      "Allowed day for operations must be set for this account type";
  public static final String OPERATION_NOT_ALLOWED = "Operation not allowed";
  public static final Long ONE = 1L;
  public static final Long ZERO = 0L;

}
