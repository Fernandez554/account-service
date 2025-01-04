package com.nttbank.microservices.accountservice;

import com.nttbank.microservices.accountservice.controller.BankAccountController;
import com.nttbank.microservices.accountservice.mapper.BankAccountMapper;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.service.BankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class BankAccountControllerTest {

  @Mock
  private BankAccountService bankAccountService;
  private BankAccountMapper bankAccountMapper;

  WebTestClient client;

  private static final String BASE_URL = "/accounts";

  @BeforeEach
  void setUp() {
    client = WebTestClient.bindToController(
        new BankAccountController(bankAccountService, bankAccountMapper)).build();
  }

  @Test
  void findAll_ShouldReturnListOfBankAccounts() {
    // Arrange: Mock the service layer to return a list of accounts
    BankAccount account1 = new BankAccount();
    account1.setId("1234");
    BankAccount account2 = new BankAccount();
    account1.setId("1234");

    Mockito.when(bankAccountService.findAll()).thenReturn(Flux.just(account1, account2));

    // Act: Call the endpoint
    client.get()
        .uri(BASE_URL)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(BankAccount.class)
        .hasSize(2)
        .contains(account1, account2);

    // Verify service interaction
    Mockito.verify(bankAccountService, Mockito.times(1)).findAll();
  }

  @Test
  void findById_ShouldReturnBankAccount() {

    String accountId = "1234";
    BankAccount account = new BankAccount();
    account.setId(accountId);
    Mockito.when(bankAccountService.findById(account.getId())).thenReturn(Mono.just(account));

    // Act: Call the endpoint
    client.get()
        .uri(BASE_URL + "/{account_id}", account.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id").isEqualTo(accountId);

  }

}
