package com.nttbank.microservices.accountservice.util;

public class Constants {

  private Constants() {
  }

  public static final String STATUS_KEY = "status";
  public static final String MESSAGE_KEY = "message";
  public static final String PATH_KEY = "path";
  public static final String ERROR_KEY = "error";
  public static final String INVALID_ACCOUNT_TYPE =
      "You cannot open an account of this type";
  public static final String NO_ACTIVE_CREDIT_CARDS =
      "Customer must have at least one active credit card to open this account";
  public static final String OPENING_ACCOUNT_RESTRICTION =
      "Customer is restricted from opening a new account of type %s";
  public static final String NO_WITHDRAW_FUNDS_AVAILABLE =
      "Account balance %s doesn't have the enough funds to cover the withdraw";
  public static final Long ONE = 1L;
  public static final Long ZERO = 0L;

}
