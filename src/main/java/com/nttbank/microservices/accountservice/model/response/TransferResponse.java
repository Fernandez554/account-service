package com.nttbank.microservices.accountservice.model.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the response returned after a successful transfer operation between two accounts.
 * This class encapsulates the details of the transfer, including the source account,
 * destination account, and the amount transferred.
 */
@Data
@Getter
@Setter
@Builder
@ToString
@JsonSerialize
@JsonDeserialize
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {

  private String fromAccount;
  private String toAccount;
  private BigDecimal amount;
}
