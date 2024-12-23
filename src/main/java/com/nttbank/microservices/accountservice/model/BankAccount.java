package com.nttbank.microservices.accountservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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

  @Pattern(regexp = "^(saving|checking|fixed)$",
      message = "Customer type must be 'saving' or 'checking' or 'fixed'")
  @NotNull(message = "Account type cannot be null")
  private String accountType;

  @NotNull(message = "Customer Identifier cannot be null")
  private String customerId;

  private BigDecimal balance;

  private Integer maxMonthlyTrans;

  private BigDecimal maintenanceFee;

  private LocalDate allowedWithdrawalDay;

  private BigDecimal withdrawAmountMax;

  private List<String> lstSigners;

  private List<String> lstHolders;

}
