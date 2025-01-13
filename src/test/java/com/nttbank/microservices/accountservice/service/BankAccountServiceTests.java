package com.nttbank.microservices.accountservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nttbank.microservices.accountservice.model.entity.AccountStatus;
import com.nttbank.microservices.accountservice.model.entity.AccountTransactions;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.model.entity.MonthlyTransactionSummary;
import com.nttbank.microservices.accountservice.model.entity.TransactionType;
import com.nttbank.microservices.accountservice.model.response.CustomerResponse;
import com.nttbank.microservices.accountservice.repo.IAccountTransactionRepo;
import com.nttbank.microservices.accountservice.repo.IBankAccountRepo;
import com.nttbank.microservices.accountservice.service.impl.BankAccountServiceImpl;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BankAccountServiceTests {

  @Mock
  private IBankAccountRepo accountRepo;

  @Mock
  private IAccountTransactionRepo transactionRepo;

  @Mock
  private CustomerService customerService;

  @Mock
  private CreditCardService creditCardService;

  @Mock
  private TransactionalOperator transactionalOperator;

  @InjectMocks
  private BankAccountServiceImpl bankAccountService;

  private BankAccount bankAccount;
  private AccountTransactions transactionDepo;
  private AccountTransactions transactionWithdraw;
  private CustomerResponse customer;

  @BeforeEach
  void setUp() {

    bankAccount = BankAccount.builder()
        .id("12345")
        .accountType("saving")
        .customerId("cust123")
        .balance(BigDecimal.valueOf(1000.00))
        .maxMonthlyTrans(10)
        .maintenanceFee(BigDecimal.valueOf(5.00))
        .transactionFee(BigDecimal.valueOf(0.50))
        .allowedDayOperation(30)
        .withdrawAmountMax(BigDecimal.valueOf(500.00))
        .lstSigners(Set.of("signer1", "signer2"))
        .lstHolders(Set.of("holder1", "holder2"))
        .monthlyTransactionSummary(new MonthlyTransactionSummary())
        .status(AccountStatus.active)
        .build();

    transactionDepo = AccountTransactions.builder()
        .id("tx12345")
        .customerId("cust001")
        .accountId("12345")
        .productName("Savings Account")
        .type(TransactionType.deposit)
        .amount(new BigDecimal("500.00"))
        .balanceAfterMovement(new BigDecimal("1500.00"))
        .description("Deposit to savings account")
        .build();

    transactionWithdraw = AccountTransactions.builder()
        .id("tx12345")
        .customerId("cust001")
        .accountId("12345")
        .productName("Savings Account")
        .type(TransactionType.withdrawal)
        .amount(new BigDecimal("500.00"))
        .balanceAfterMovement(new BigDecimal("1000.00"))
        .description("Deposit to savings account")
        .build();

    customer = CustomerResponse.builder()
        .id("123")
        .type("personal")
        .profile("basic")
        .name("John Doe")
        .phone("123-456-7890")
        .email("john.doe@example.com")
        .address("123 Main St, Anytown, USA")
        .dateOfBirth("1990-01-01")
        .build();
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSave() {
    when(customerService.findCustomerById(anyString())).thenReturn(Mono.just(customer));
    when(
        accountRepo.countByCustomerIdAndAccountTypeAndStatus(anyString(), anyString(), anyString()))
        .thenReturn(Mono.just(0L));
    when(accountRepo.save(any(BankAccount.class))).thenReturn(Mono.just(bankAccount));

    StepVerifier.create(bankAccountService.save(bankAccount))
        .expectNextMatches(savedAccount -> {
          // Verify the saved account's properties
          assertThat(savedAccount.getId()).isEqualTo(bankAccount.getId());
          assertThat(savedAccount.getAccountType()).isEqualTo(bankAccount.getAccountType());
          return true;
        })
        .verifyComplete();
  }

  @Test
  void testFindAll() {
    BankAccount account = new BankAccount();
    when(accountRepo.findAll()).thenReturn(Flux.just(account));

    StepVerifier.create(bankAccountService.findAll())
        .expectNext(account)
        .verifyComplete();
  }

  @Test
  void testFindById() {
    BankAccount account = new BankAccount();
    when(accountRepo.findById(anyString())).thenReturn(Mono.just(account));

    StepVerifier.create(bankAccountService.findById("accountId"))
        .expectNext(account)
        .verifyComplete();
  }

}
