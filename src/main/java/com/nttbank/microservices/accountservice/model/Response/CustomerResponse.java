package com.nttbank.microservices.accountservice.model.Response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@JsonSerialize
@JsonDeserialize
public class CustomerResponse {

  private String id;
  private String type;
  private String name;
  private String phone;
  private String email;
  private String address;
  private String dateOfBirth;

}