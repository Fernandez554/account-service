package com.nttbank.microservices.accountservice.controller;

import com.nttbank.microservices.accountservice.exception.CustomerNotFoundException;
import com.nttbank.microservices.accountservice.model.Response.BankAccountResponse;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.service.BankAccountService;
import com.nttbank.microservices.accountservice.service.CustomerService;
import com.nttbank.microservices.accountservice.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Bank Account Controller", description = "Manage bank accounts")
public class BankAccountController {

  private static final Logger log = LoggerFactory.getLogger(BankAccountController.class);
  private final BankAccountService bankAccountService;
  private final CustomerService customerService;

  /**
   * Retrieves all bank accounts.
   *
   * @return A {@link Mono} containing a {@link ResponseEntity} with a {@link Flux} of
   * {@link BankAccount}. If no accounts are found, returns a response with HTTP 204 (No Content).
   */

  @GetMapping
  @Operation(summary = "Retrieve all bank accounts",
      description = "Fetches all bank accounts as a reactive Flux.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of bank accounts"),
      @ApiResponse(responseCode = "204", description = "No content available (empty list)")
  })
  public Mono<ResponseEntity<Flux<BankAccount>>> findAll() {
    Flux<BankAccount> accountList = bankAccountService.findAll();

    return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(accountList))
        .defaultIfEmpty(ResponseEntity.noContent().build());
  }

  /**
   * Retrieves a specific bank account by its ID.
   *
   * @param id The ID of the bank account to retrieve.
   * @return A {@link Mono} containing a {@link ResponseEntity} with the {@link BankAccount}. If the
   * account does not exist, returns a response with HTTP 404 (Not Found).
   */
  @GetMapping("/{account_id}")
  @Operation(summary = "Retrieve a specific bank account",
      description = "Fetches a bank account by its unique ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved the bank account"),
      @ApiResponse(responseCode = "404", description = "Bank account not found")
  })
  public Mono<ResponseEntity<BankAccount>> findById(@Valid @PathVariable("account_id") String id) {
    return bankAccountService.findById(id)
        .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Creates a new bank account.
   *
   * @param account The {@link BankAccount} object to create.
   * @param req     The {@link ServerHttpRequest} containing the request details.
   * @return A {@link Mono} containing a {@link ResponseEntity} with the created
   * {@link BankAccount}. The response includes the location of the newly created resource with HTTP
   * 201 (Created). If creation fails, returns a response with HTTP 404 (Not Found).
   */
  @Operation(summary = "Create a new bank account",
      description = "Creates a new bank account and returns the newly created resource.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully created the bank account"),
      @ApiResponse(responseCode = "404", description = "Creation failed")
  })
  @PostMapping
  public Mono<ResponseEntity<BankAccount>> save(
      @Valid @RequestBody BankAccount account,
      final ServerHttpRequest req) {
    //return bankAccountService.save(account, req);
    return bankAccountService.save(account, req)
            .map(c -> ResponseEntity.created(
                    URI.create(req.getURI().toString().concat("/").concat(c.getId())))
                .contentType(MediaType.APPLICATION_JSON).body(c))
            .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
//    return customerService.findCustomerById(account.getCustomerId())
//        .flatMap(customer -> bankAccountService.save(account, req)
//            .map(c -> ResponseEntity.created(
//                    URI.create(req.getURI().toString().concat("/").concat(c.getId())))
//                .contentType(MediaType.APPLICATION_JSON).body(c))
//            .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()))
//        .onErrorResume(e -> ResponseUtil.createNotFoundResponse(BankAccount.class));
  }


  /**
   * Updates an existing bank account by its ID.
   *
   * @param id      The ID of the bank account to update.
   * @param account The {@link BankAccount} object containing updated details.
   * @return A {@link Mono} containing a {@link ResponseEntity} with the updated
   * {@link BankAccount}. If the account does not exist, returns a response with HTTP 404 (Not
   * Found).
   */
  @Operation(summary = "Update an existing bank account",
      description = "Updates a bank account by its ID and returns the updated details.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated the bank account"),
      @ApiResponse(responseCode = "404", description = "Bank account not found")
  })
  @PutMapping("/{account_id}")
  public Mono<ResponseEntity<BankAccount>> update(@Valid @PathVariable("account_id") String id,
      @Valid @RequestBody BankAccount account) {
    account.setId(id);

    Mono<BankAccount> monoBody = Mono.just(account);
    Mono<BankAccount> monoDb = bankAccountService.findById(id);

    return monoDb.zipWith(monoBody, (db, c) -> {
          db.setId(id);
          db.setAccountType(c.getAccountType());
          db.setCustomerId(c.getCustomerId());
          db.setBalance(c.getBalance());
          db.setMaxMonthlyTrans(c.getMaxMonthlyTrans());
          db.setMaintenanceFee(c.getMaintenanceFee());
          db.setAllowedWithdrawalDay(c.getAllowedWithdrawalDay());
          db.setWithdrawAmountMax(c.getWithdrawAmountMax());
          db.setLstSigners(c.getLstSigners());
          db.setLstHolders(c.getLstHolders());
          return db;
        }).flatMap(bankAccountService::update)
        .map(e -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(e))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Deletes a bank account by its ID.
   *
   * @param id The ID of the bank account to delete.
   * @return A {@link Mono} containing a {@link ResponseEntity}. If the account is successfully
   * deleted, returns HTTP 204 (No Content). If the account does not exist, returns a response with
   * HTTP 404 (Not Found).
   */
  @Operation(summary = "Delete a bank account",
      description = "Deletes a bank account by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Successfully deleted the bank account"),
      @ApiResponse(responseCode = "404", description = "Bank account not found")
  })
  @DeleteMapping("/{account_id}")
  public Mono<ResponseEntity<Void>> delete(@PathVariable("account_id") String id) {
    return bankAccountService.findById(id).flatMap(
            c -> bankAccountService.delete(c.getId()).thenReturn(
                new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

}
