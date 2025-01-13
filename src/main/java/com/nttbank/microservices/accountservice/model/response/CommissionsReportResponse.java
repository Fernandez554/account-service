package com.nttbank.microservices.accountservice.model.response;

import com.nttbank.microservices.accountservice.model.entity.AccountTransactions;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommissionsReportResponse {

  private String description;
  private String productName;
  @Builder.Default
  private LocalDateTime generatedAt = LocalDateTime.now();
  private LocalDate startDate;
  private LocalDate endDate;
  @Builder.Default
  private String status = "active";
  private List<AccountTransactions> lstReportDay;

}
