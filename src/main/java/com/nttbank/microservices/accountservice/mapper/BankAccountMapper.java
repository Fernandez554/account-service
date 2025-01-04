package com.nttbank.microservices.accountservice.mapper;

import com.nttbank.microservices.accountservice.dto.BankAccountDTO;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = ComponentModel.SPRING)
public interface BankAccountMapper {

  BankAccountMapper INSTANCE = Mappers.getMapper(BankAccountMapper.class);

  BankAccount accountDtoToAccount(BankAccountDTO accountDTO);
}
