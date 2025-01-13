package com.nttbank.microservices.accountservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.nttbank.microservices.accountservice.dto.BankAccountDTO;
import com.nttbank.microservices.accountservice.mapper.BankAccountMapper;
import com.nttbank.microservices.accountservice.model.entity.AccountStatus;
import com.nttbank.microservices.accountservice.model.entity.AccountTransactions;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.model.entity.MonthlyTransactionSummary;
import com.nttbank.microservices.accountservice.model.response.CommissionsReportResponse;
import com.nttbank.microservices.accountservice.service.BankAccountService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class BankAccountControllerTests {

  @Mock
  private BankAccountService bankAccountService;

  @Mock
  private BankAccountMapper bankAccountMapper;

  WebTestClient client;

  private static final String BASE_URL = "/accounts";
  private static final String ACCOUNT_ID_PATH = "/{account_id}";

  BankAccount bankAccount;
  BankAccountDTO bankAccountDTO;

  @BeforeEach
  void setUp() {
    client = WebTestClient.bindToController(
            new BankAccountController(bankAccountService, bankAccountMapper))
        .build();
    bankAccount = BankAccount.builder()
        .id("1234")
        .accountType("saving")
        .customerId("1111")
        .balance(new BigDecimal("1000.00"))
        .maxMonthlyTrans(10)
        .maintenanceFee(new BigDecimal("5.00"))
        .transactionFee(new BigDecimal("0.50"))
        .allowedDayOperation(30)
        .withdrawAmountMax(new BigDecimal("500.00"))
        .lstSigners(Set.of("signer1", "signer2"))
        .lstHolders(Set.of("holder1", "holder2"))
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .monthlyTransactionSummary(new MonthlyTransactionSummary())
        .status(AccountStatus.active)
        .build();
  }


  @Test
  void findAll_ShouldReturnListOfBankAccounts() {
    BankAccount account1 = new BankAccount();
    account1.setId("1234");
    BankAccount account2 = new BankAccount();
    account2.setId("5678");

    when(bankAccountService.findAll()).thenReturn(Flux.just(account1, account2));

    client.get()
        .uri(BASE_URL)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(BankAccount.class)
        .hasSize(2)
        .contains(account1, account2);

    Mockito.verify(bankAccountService, Mockito.times(1)).findAll();
  }


  @Test
  void findById_ShouldReturnBankAccount() {

    String accountId = "1234";
    BankAccount account = new BankAccount();
    account.setId(accountId);
    when(bankAccountService.findById(account.getId())).thenReturn(Mono.just(account));

    client.get()
        .uri(BASE_URL + "/{account_id}", account.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id").isEqualTo(accountId);

  }

  @Test
  void save_ShouldCreateNewBankAccount() {

    when(bankAccountMapper.accountDtoToAccount(any(BankAccountDTO.class)))
        .thenReturn(bankAccount);
    when(bankAccountService.save(any(BankAccount.class)))
        .thenReturn(Mono.just(bankAccount));

    client.post()
        .uri(BASE_URL)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(bankAccount)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().valueEquals("Location", BASE_URL + "/1234")
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id").isEqualTo("1234");

  }

  @Test
  void delete_ShouldReturnNoContent() {
    // Arrange
    String accountId = "1234";
    BankAccount account = new BankAccount();
    account.setId(accountId);

    when(bankAccountService.findById(accountId)).thenReturn(Mono.just(account));
    when(bankAccountService.delete(accountId)).thenReturn(Mono.empty());

    client.delete()
        .uri(BASE_URL + "/{account_id}", accountId)
        .exchange()
        .expectStatus().isNoContent();

    Mockito.verify(bankAccountService, Mockito.times(1)).findById(accountId);
    Mockito.verify(bankAccountService, Mockito.times(1)).delete(accountId);
  }

  @Test
  void saveSigner_ShouldAddSignerToAccount() {
    // Arrange
    String accountId = "1234";
    String signerId = "5678";
    BankAccount account = new BankAccount();
    account.setId(accountId);

    Mockito.when(bankAccountService.saveSigner(accountId, signerId)).thenReturn(Mono.just(account));

    client.post()
        .uri(BASE_URL + "/{account_id}/signers/{signer_id}", accountId, signerId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id").isEqualTo(accountId);

    Mockito.verify(bankAccountService, Mockito.times(1)).saveSigner(accountId, signerId);
  }

  @Test
  void update_ShouldUpdateBankAccount() {
    String accountId = "1234";
    when(bankAccountMapper.accountDtoToAccount(Mockito.any(BankAccountDTO.class))).thenReturn(
        bankAccount);
    when(bankAccountService.findById(accountId)).thenReturn(Mono.just(bankAccount));
    when(bankAccountService.update(any(BankAccount.class))).thenReturn(Mono.just(bankAccount));

    client.put()
        .uri(BASE_URL + "/{account_id}", accountId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(bankAccount))
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id").isEqualTo(accountId);


  }

  @Test
  void findMovements_ShouldReturnAccountTransactions() {
    // Arrange
    String accountId = "1234";
    AccountTransactions transaction1 = new AccountTransactions();
    transaction1.setId("trans1");
    AccountTransactions transaction2 = new AccountTransactions();
    transaction2.setId("trans2");

    Mockito.when(bankAccountService.findAccountTransactions(accountId))
        .thenReturn(Flux.just(transaction1, transaction2));

    client.get()
        .uri(BASE_URL + "/{account_id}/transactions", accountId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(AccountTransactions.class)
        .hasSize(2)
        .contains(transaction1, transaction2);

    Mockito.verify(bankAccountService, Mockito.times(1)).findAccountTransactions(accountId);
  }

  @Test
  void deleteSigner_ShouldRemoveSignerFromAccount() {
    // Arrange
    String accountId = "1234";
    String signerId = "5678";
    BankAccount account = new BankAccount();
    account.setId(accountId);

    Mockito.when(bankAccountService.deleteSigner(accountId, signerId))
        .thenReturn(Mono.just(account));

    client.delete()
        .uri(BASE_URL + "/{account_id}/signers/{signer_id}", accountId, signerId)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id").isEqualTo(accountId);

    Mockito.verify(bankAccountService, Mockito.times(1)).deleteSigner(accountId, signerId);
  }

  @Test
  void reportCommisionsByProduct_ShouldReturnCommissionsReport() {
    LocalDate startDate = LocalDate.of(2023, 1, 1);
    LocalDate endDate = LocalDate.of(2023, 12, 31);
    String productName = "Savings";
    CommissionsReportResponse reportResponse = new CommissionsReportResponse();
    reportResponse.setProductName(productName);

    when(bankAccountService.generateReportCommissionsProduct(eq(startDate), eq(endDate),
        eq(productName)))
        .thenReturn(Mono.just(reportResponse));

    client.post()
        .uri(uriBuilder -> uriBuilder.path(BASE_URL + "/reports/commissions")
            .queryParam("startDate", startDate)
            .queryParam("endDate", endDate)
            .queryParam("productName", productName)
            .build())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.productName").isEqualTo(productName);

    Mockito.verify(bankAccountService, Mockito.times(1))
        .generateReportCommissionsProduct(eq(startDate), eq(endDate), eq(productName));
  }

}
