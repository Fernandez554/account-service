package com.nttbank.microservices.accountservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a bank account with various attributes including account type, customer ID, balance,
 * transaction limits, and other properties. This class is used to interact with the 'account'
 * collection in the MongoDB database.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountDTO {

  private String id;

  @Pattern(regexp = "^(saving|checking|fixed)$",
      message = "Account type must be 'saving', 'checking', or 'fixed'")
  @NotNull(message = "Account type cannot be null")
  private String accountType;

  @NotNull(message = "Customer Identifier cannot be null")
  @NotEmpty(message = "Customer Identifier cannot be empty")
  private String customerId;

  @Min(value = 0, message = "Balance must be greater than or equal to 0")
  private BigDecimal balance;

  @Min(value = 0, message = "Max. Monthly transactions by account cannot be lower or equal to 0")
  private Integer maxMonthlyTrans;

  private BigDecimal maintenanceFee;

  @Min(value = 1, message = "Allowed day of operation must be between 1 and 31")
  @Max(value = 31, message = "Allowed day of operation must be between 1 and 31")
  private Integer allowedDayOperation;

  private BigDecimal withdrawAmountMax;

  private List<String> lstSigners;

  private List<String> lstHolders;

  private LocalDateTime creationDate;
}
