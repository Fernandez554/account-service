package com.nttbank.microservices.accountservice.service.impl;

import com.nttbank.microservices.accountservice.action.IOpenable;
import com.nttbank.microservices.accountservice.factory.BackAccountFactory;
import com.nttbank.microservices.accountservice.model.Response.CustomerResponse;
import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.repo.IBankAccountRepo;
import com.nttbank.microservices.accountservice.service.BankAccountService;
import com.nttbank.microservices.accountservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

  private final IBankAccountRepo repo;
  private final CustomerService customerService;

  public Mono<CustomerResponse> getCustomerById(String customerId) {
    return customerService.findCustomerById(customerId).onErrorResume(e -> {
      return Mono.just(new CustomerResponse());
    });
  }

  @Override
  public Mono<BankAccount> save(BankAccount account, ServerHttpRequest req) {
    Mono<CustomerResponse> customer = customerService.findCustomerById(account.getCustomerId());
    Mono<BankAccount> bankAccount = Mono.just(
        BackAccountFactory.createAccount(account.getAccountType(), account));

    return customer.flatMap(c ->
        bankAccount.flatMap(b ->
            repo.findByCustomerIdAndAccountType(c.getId(), b.getAccountType())
                .count()
                .flatMap(totalAccounts -> {
                  if (b instanceof IOpenable) {
                    // Validate if the account can be opened
                    boolean canOpen = ((IOpenable) b).openAccount(totalAccounts, c.getType());
                    if (canOpen) {
                      // Save the account if validation passes
                      return repo.save(b);
                    } else {
                      // Return an error if the account cannot be opened
                      return Mono.error(new IllegalArgumentException("Account cannot be opened due to business rules."));
                    }
                  }
                  return Mono.error(new IllegalStateException("BankAccount is not openable."));
                })
        )
    );
//          .doOnNext(totalAccounts -> {
//
////            if (b instanceof IOpenable) {
////              ((IOpenable) c).openAccount(totalAccounts);
////            }
//          }).subscribe();
//      //return repo.save(c);
//    }).subscribe();

//    return Mono.just(BackAccountFactory.createAccount(account.getAccountType(), account))
//        .flatMap(c -> {
//
//          return repo.save(c);
//        });
    //BankAccount c = ;
    //return repo.findByCustomerIdAndAccountType(account.getCustomerId(),)

//    return customerService.findCustomerById(account.getCustomerId())
//        .onErrorResume(e -> {
//          BankAccountResponse<BankAccount> errorResponse = new BankAccountResponse<>(
//              HttpStatus.NOT_FOUND.value(), "Error: " + e.getMessage(), null);
//          return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
//        });

    // return repo.save(account);

  }

  @Override
  public Mono<BankAccount> update(BankAccount t) {
    return repo.save(t);
  }

  @Override
  public Flux<BankAccount> findAll() {
    return repo.findAll();
  }

  @Override
  public Mono<BankAccount> findById(String customerId) {
    return repo.findById(customerId);
  }

  @Override
  public Mono<Void> delete(String customerId) {
    return repo.deleteById(customerId);
  }

}
