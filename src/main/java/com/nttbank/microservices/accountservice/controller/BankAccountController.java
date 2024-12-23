package com.nttbank.microservices.accountservice.controller;

import com.nttbank.microservices.accountservice.model.BankAccount;
import com.nttbank.microservices.accountservice.service.BankAccountService;
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

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Validated
public class BankAccountController {

  private final BankAccountService service;


  /**
   * Retrieves all bank accounts.
   *
   * @return A {@link Mono} containing a {@link ResponseEntity} with a {@link Flux} of
   *     {@link BankAccount}. If no accounts are found,
   *     returns a response with HTTP 204 (No Content).
   */
  @GetMapping
  public Mono<ResponseEntity<Flux<BankAccount>>> findAll() {
    Flux<BankAccount> accountList = service.findAll();

    return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(accountList))
        .defaultIfEmpty(ResponseEntity.noContent().build());
  }

  /**
   * Retrieves a specific bank account by its ID.
   *
   * @param id The ID of the bank account to retrieve.
   * @return A {@link Mono} containing a {@link ResponseEntity} with the {@link BankAccount}. If the
   *     account does not exist, returns a response with HTTP 404 (Not Found).
   */
  @GetMapping("/{account_id}")
  public Mono<ResponseEntity<BankAccount>> findById(@Valid @PathVariable("account_id") String id) {
    return service.findById(id)
        .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Creates a new bank account.
   *
   * @param account The {@link BankAccount} object to create.
   * @param req     The {@link ServerHttpRequest} containing the request details.
   * @return A {@link Mono} containing a {@link ResponseEntity} with the created
   *     {@link BankAccount}. The response includes the location of the newly created
   *     resource with HTTP 201 (Created).
   *     If creation fails, returns a response with HTTP 404 (Not Found).
   */
  @PostMapping
  public Mono<ResponseEntity<BankAccount>> save(@Valid @RequestBody BankAccount account,
      final ServerHttpRequest req) {
    return service.save(account).map(c -> ResponseEntity.created(
                URI.create(req.getURI().toString().concat("/").concat(c.getId())))
            .contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Updates an existing bank account by its ID.
   *
   * @param id      The ID of the bank account to update.
   * @param account The {@link BankAccount} object containing updated details.
   * @return A {@link Mono} containing a {@link ResponseEntity} with the updated
   *     {@link BankAccount}. If the account does not exist, returns a response with HTTP 404 (Not
   *     Found).
   */
  @PutMapping("/{account_id}")
  public Mono<ResponseEntity<BankAccount>> update(@Valid @PathVariable("account_id") String id,
      @Valid @RequestBody BankAccount account) {
    account.setId(id);

    Mono<BankAccount> monoBody = Mono.just(account);
    Mono<BankAccount> monoDb = service.findById(id);

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
    }).flatMap(service::update)
      .map(e -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(e))
      .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * Deletes a bank account by its ID.
   *
   * @param id The ID of the bank account to delete.
   * @return A {@link Mono} containing a {@link ResponseEntity}. If the account is successfully
   *      deleted, returns HTTP 204 (No Content). If the account does not exist, returns a response
   *      with HTTP 404 (Not Found).
   */
  @DeleteMapping("/{account_id}")
  public Mono<ResponseEntity<Void>> delete(@PathVariable("account_id") String id) {
    return service.findById(id).flatMap(
            c -> service.delete(c.getId()).thenReturn(
                new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

}