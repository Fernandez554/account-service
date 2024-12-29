package com.nttbank.microservices.accountservice.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "account")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class BankAccount {

  @EqualsAndHashCode.Include
  @Id
  private String id;

  @Pattern(regexp = "^(saving|checking|fixed|vip|pyme)$",
      message = "Account type must be 'saving' or 'checking' or 'fixed' or 'vip' or 'pyme'")
  @NotNull(message = "Account type cannot be null")
  private String accountType;

  @NotNull(message = "Customer Identifier cannot be null")
  @NotEmpty(message = "Customer Identifier cannt be empty")
  private String customerId;

  @Min(value = 0, message = "Balance must be greater than or equal to 0")
  @Builder.Default
  private BigDecimal balance = BigDecimal.ZERO;

  private Integer maxMonthlyTrans;

  private BigDecimal maintenanceFee;

  private LocalDate allowedWithdrawalDay;

  private BigDecimal withdrawAmountMax;

  private List<String> lstSigners;

  private List<String> lstHolders;

}