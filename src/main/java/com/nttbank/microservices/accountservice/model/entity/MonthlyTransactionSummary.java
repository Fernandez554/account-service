package com.nttbank.microservices.accountservice.model.entity;


import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MonthlyTransactionSummary {
  @Builder.Default
  private Integer month = LocalDate.now().getMonth().getValue();
  @Builder.Default
  private Integer year = LocalDate.now().getYear();
  @Builder.Default
  private Integer numberOfTransactions = BigDecimal.ZERO.intValue();
}
