package com.nttbank.microservices.accountservice.controller;

import com.nttbank.microservices.accountservice.dto.BankAccountDTO;
import com.nttbank.microservices.accountservice.mapper.BankAccountMapper;
import com.nttbank.microservices.accountservice.model.entity.AccountTransactions;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.model.response.CommissionsReportResponse;
import com.nttbank.microservices.accountservice.service.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST Controller for managing bank accounts.
 *
 * <p>
 * Provides endpoints for retrieving, creating, updating, deleting, and performing operations on
 * bank accounts such as withdrawals, deposits, and transfers.
 * </p>
 */
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Bank Account Controller", description = "Manage bank accounts")
public class BankAccountController {

  private final BankAccountService bankAccountService;
  private final BankAccountMapper bankAccountMapper;

  /**
   * Retrieves all bank accounts.
   *
   * @return a {@link Mono} containing a {@link ResponseEntity} with a {@link Flux} of all
   *     {@link BankAccount}.
   */
  @Operation(summary = "Retrieve all bank accounts",
      description = "Fetches a list of all bank accounts.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Accounts found",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "204", description = "No accounts found")
  })
  @GetMapping
  public Mono<ResponseEntity<Flux<BankAccount>>> findAll() {
    Flux<BankAccount> accountList = bankAccountService.findAll();

    return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(accountList))
        .defaultIfEmpty(ResponseEntity.noContent().build());
  }

  /**
   * Creates a new bank account.
   *
   * @param bankAccountDTO the bank account to create.
   * @param req            the HTTP request to generate the location URI.
   * @return a {@link Mono} containing a {@link ResponseEntity} with the created
   *     {@link BankAccount}.
   */
  @Operation(summary = "Create a new bank account",
      description = "Adds a new bank account.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Account created",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "401", description = "Unauthorized action")
  })
  @PostMapping
  public Mono<ResponseEntity<BankAccount>> save(@Valid @RequestBody BankAccountDTO bankAccountDTO,
      final ServerHttpRequest req) {
    return bankAccountService.save(bankAccountMapper.accountDtoToAccount(bankAccountDTO))
        .map(c -> ResponseEntity.created(
                URI.create(req.getURI().toString().concat("/").concat(c.getId())))
            .contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  /**
   * Deletes a bank account by ID.
   *
   * @param id the ID of the account to delete.
   * @return a {@link Mono} containing a {@link ResponseEntity} with status code 204 if successful,
   *     or 404 if not found.
   */
  @Operation(summary = "Delete a bank account",
      description = "Deletes a bank account using its ID.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Account deleted"),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @DeleteMapping("/{account_id}")
  public Mono<ResponseEntity<Void>> delete(@PathVariable("account_id") String id) {
    return bankAccountService.findById(id)
        .flatMap(c -> bankAccountService.delete(c.getId())
            .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Retrieves a bank account by its ID.
   *
   * @param id the unique identifier of the bank account.
   * @return a {@link Mono} containing a {@link ResponseEntity} with the {@link BankAccount}, or a
   * 404 if not found.
   */
  @Operation(summary = "Retrieve a bank account by ID",
      description = "Fetches a bank account using its unique ID.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Account found",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @GetMapping("/{account_id}")
  public Mono<ResponseEntity<BankAccount>> findById(@Valid @PathVariable("account_id") String id) {
    return bankAccountService.findById(id)
        .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Updates an existing bank account.
   *
   * @param id             the ID of the account to update.
   * @param bankAccountDTO the updated account details.
   * @return a {@link Mono} containing a {@link ResponseEntity} with the updated
   * {@link BankAccount}.
   */
  @Operation(summary = "Update an existing bank account",
      description = "Updates the details of an existing bank account.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Account updated",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @PutMapping("/{account_id}")
  public Mono<ResponseEntity<BankAccount>> update(@Valid @PathVariable("account_id") String id,
      @Valid @RequestBody BankAccountDTO bankAccountDTO) {
    bankAccountDTO.setId(id);

    Mono<BankAccount> monoBody = Mono.just(bankAccountMapper.accountDtoToAccount(bankAccountDTO));
    Mono<BankAccount> monoDb = bankAccountService.findById(id);

    return monoDb.zipWith(monoBody, (db, c) -> {
          db.setId(id);
          db.setAccountType(c.getAccountType());
          db.setCustomerId(c.getCustomerId());
          db.setBalance(c.getBalance());
          db.setMaxMonthlyTrans(c.getMaxMonthlyTrans());
          db.setMaintenanceFee(c.getMaintenanceFee());
          db.setAllowedDayOperation(c.getAllowedDayOperation());
          db.setWithdrawAmountMax(c.getWithdrawAmountMax());
          db.setLstSigners(c.getLstSigners());
          db.setLstHolders(c.getLstHolders());
          db.setTransactionFee(c.getTransactionFee());
          db.setMonthlyTransactionSummary(c.getMonthlyTransactionSummary());
          return db;
        }).flatMap(bankAccountService::update)
        .map(e -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(e))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Deposits an amount into a bank account.
   *
   * @param accountId the ID of the account to deposit into.
   * @param amount    the amount to deposit.
   * @return a {@link Mono} containing a {@link ResponseEntity} with the updated
   * {@link BankAccount}.
   */
  @Operation(summary = "Deposit into a bank account",
      description = "Deposits an amount into the specified account.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Deposit successful",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @PostMapping("/{account_id}/deposit")
  public Mono<ResponseEntity<AccountTransactions>> deposit(
      @PathVariable("account_id") String accountId,
      @RequestParam("amount")
      @NotNull @Positive(message = "Deposit amount must be greater than zero") BigDecimal amount) {

    return bankAccountService.deposit(accountId, amount)
        .map(e -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(e))
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * Withdraws an amount from a bank account.
   *
   * @param accountId the ID of the account to withdraw from.
   * @param amount    the amount to withdraw.
   * @return a {@link Mono} containing a {@link ResponseEntity} with the updated
   * {@link BankAccount}.
   */
  @Operation(summary = "Withdraw from a bank account",
      description = "Withdraws an amount from the specified account.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Withdrawal successful",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @PostMapping("/{account_id}/withdraw")
  public Mono<ResponseEntity<AccountTransactions>> withdraw(
      @PathVariable("account_id") String accountId,
      @RequestParam("amount")
      @NotNull
      @Positive(message = "Withdrawal amount must be greater than zero") BigDecimal amount) {

    return bankAccountService.withdraw(accountId, amount)
        .map(e -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(e))
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * Transfers an amount between two bank accounts.
   *
   * @param fromAccountId the ID of the account to transfer from.
   * @param toAccountId   the ID of the account to transfer to.
   * @param amount        the amount to transfer.
   * @return a {@link Mono} containing a {@link ResponseEntity} with a {@link AccountTransactions}.
   */
  @Operation(summary = "Transfer between accounts",
      description = "Transfers an amount from one account to another.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transfer successful",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @PostMapping("/{from_account_id}/{to_account_id}/transfer")
  public Mono<ResponseEntity<AccountTransactions>> transfer(
      @PathVariable("from_account_id") String fromAccountId,
      @PathVariable("to_account_id") String toAccountId,
      @RequestParam("amount")
      @NotNull @Positive(message = "Transfer amount must be greater than zero") BigDecimal amount) {

    return bankAccountService.transfer(fromAccountId, toAccountId, amount)
        .map(e -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(e))
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * Retrieve movements for an account.
   *
   * @param accountId the ID of the account to retrieve movements for.
   * @return a {@link Mono} containing a {@link ResponseEntity} with the movements for the account.
   */
  @Operation(summary = "Retrieve movements for an account",
      description = "Fetches the movements for the specified account.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Movements found",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @GetMapping("/{account_id}/transactions")
  public Mono<ResponseEntity<Flux<AccountTransactions>>> findMovements(
      @Valid @PathVariable("account_id") String accountId) {
    Flux<AccountTransactions> movements = bankAccountService.findAccountTransactions(accountId);
    return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(movements))
        .defaultIfEmpty(ResponseEntity.noContent().build());
  }

  /**
   * Save a holder to an account.
   *
   * @param accountId the ID of the account to save the holder to.
   * @param holderId  the ID of the holder to save.
   * @return a {@link Mono} containing a {@link ResponseEntity} with the updated
   * {@link BankAccount}.
   */
  @Operation(summary = "Save a holder to an account",
      description = "Adds a holder to the specified account.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Holder saved",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @PostMapping("/{account_id}/holders/{holder_id}")
  public Mono<ResponseEntity<BankAccount>> saveHolder(
      @Valid @PathVariable("account_id") String accountId,
      @Valid @PathVariable("holder_id") String holderId) {
    return bankAccountService.saveHolder(accountId, holderId)
        .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Delete a holder from an account.
   *
   * @param accountId the ID of the account to delete the holder from.
   * @param holderId  the ID of the holder to delete.
   * @return a {@link Mono} containing a {@link ResponseEntity} with the updated
   * {@link BankAccount}.
   */
  @Operation(summary = "Delete a holder from an account",
      description = "Removes a holder from the specified account.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Holder deleted",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @DeleteMapping("/{account_id}/holders/{holder_id}")
  public Mono<ResponseEntity<BankAccount>> deleteHolder(
      @Valid @PathVariable("account_id") String accountId,
      @Valid @PathVariable("holder_id") String holderId) {
    return bankAccountService.deleteHolder(accountId, holderId)
        .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Save a signer to an account.
   *
   * @param accountId the ID of the account to save the signer to.
   * @param signerId  the ID of the signer to save.
   * @return a {@link Mono} containing a {@link ResponseEntity} with the updated
   * {@link BankAccount}.
   */
  @Operation(summary = "Save a signer to an account",
      description = "Adds a signer to the specified account.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Signer saved",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @PostMapping("/{account_id}/signers/{signer_id}")
  public Mono<ResponseEntity<BankAccount>> saveSigner(
      @Valid @PathVariable("account_id") String accountId,
      @Valid @PathVariable("signer_id") String signerId) {
    return bankAccountService.saveSigner(accountId, signerId)
        .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Delete a signer from an account.
   *
   * @param accountId the ID of the account to delete the signer from.
   * @param signerId  the ID of the signer to delete.
   * @return a {@link Mono} containing a {@link ResponseEntity} with the updated
   * {@link BankAccount}.
   */
  @Operation(summary = "Delete a signer from an account",
      description = "Removes a signer from the specified account.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Signer deleted",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @DeleteMapping("/{account_id}/signers/{signer_id}")
  public Mono<ResponseEntity<BankAccount>> deleteSigner(
      @Valid @PathVariable("account_id") String accountId,
      @Valid @PathVariable("signer_id") String signerId) {
    return bankAccountService.deleteSigner(accountId, signerId)
        .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping("/reports/commissions")
  public Mono<ResponseEntity<CommissionsReportResponse>> reportCommisionsByProduct(
      @RequestParam("startDate")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
      @RequestParam("endDate")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate,
      @RequestParam("productName")
      @NotNull(message = "productName cannot be null") String productName) {
    return bankAccountService.generateReportCommissionsProduct(startDate, endDate, productName)
        .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }


}
