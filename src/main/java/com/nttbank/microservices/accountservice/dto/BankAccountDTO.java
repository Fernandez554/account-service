package com.nttbank.microservices.accountservice.dto;

import com.nttbank.microservices.accountservice.model.entity.MonthlyTransactionSummary;
import com.nttbank.microservices.accountservice.model.entity.AccountTransactions;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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

  @NotNull(message = "Max. Monthly transactions cannot be null")
  @Min(value = 1, message = "Max. Monthly transactions by account cannot be  lower or equal to 0")
  private Integer maxMonthlyTrans;

  @Builder.Default
  private BigDecimal maintenanceFee = BigDecimal.ZERO;

  @NotNull(message = "Transaction fee cannot be null")
  @DecimalMin(value = "0.01", message = "The transaction fee must be at least 0.01.")
  @DecimalMax(value = "100.00", message = "The transaction fee must be less than or equal to 100.")
  private BigDecimal transactionFee;

  @Min(value = 1, message = "Allowed day of operation must be between 1 and 31")
  @Max(value = 31, message = "Allowed day of operation must be between 1 and 31")
  private Integer allowedDayOperation;

  private BigDecimal withdrawAmountMax;

  private Set<String> lstSigners;

  private Set<String> lstHolders;

  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

  @Builder.Default
  private LocalDateTime updatedAt = LocalDateTime.now();

  private MonthlyTransactionSummary monthlyTransactionSummary;

  private List<AccountTransactions> lstTransactions;
}
