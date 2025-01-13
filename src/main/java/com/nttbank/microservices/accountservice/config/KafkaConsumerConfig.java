package com.nttbank.microservices.accountservice.config;


import com.nttbank.microservices.accountservice.model.entity.BankAccount;
import com.nttbank.microservices.accountservice.repo.IBankAccountRepo;
import com.nttbank.microservices.accountservice.service.BankAccountService;
import com.nttbank.microservices.accountservice.util.KafkaUtil;
import com.nttbank.microservices.commonlibrary.event.CreateBankAccountEvent;
import com.nttbank.microservices.commonlibrary.event.CreateWalletEvent;
import com.nttbank.microservices.commonlibrary.event.GenericEvent;
import com.nttbank.microservices.commonlibrary.event.WalletTransactionEvent;
import com.nttbank.microservices.commonlibrary.event.WalletTransferEvent;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

  private final IBankAccountRepo bankAccountRepo;
  private final BankAccountService bankAccountService;
  private final KafkaUtil kafkaUtil;

  @Value("${kafka.nttbank.server:127.0.0.1}")
  private String kafkaServer;
  @Value("${kafka.nttbank.port:9092}")
  private String kafkaPort;
  @Value("${kafka.nttbank.topic.consumer:nttbank}")
  private String topicName;

  @Bean
  public ConsumerFactory<String, GenericEvent<? extends GenericEvent>> consumerFactory() {
    Map<String, Object> kafkaProperties = new HashMap<>();
    kafkaProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer + ":" + kafkaPort);
    kafkaProperties.put(ConsumerConfig.GROUP_ID_CONFIG, topicName);

    kafkaProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        ErrorHandlingDeserializer.class);
    kafkaProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        ErrorHandlingDeserializer.class);

    kafkaProperties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, JsonDeserializer.class);
    kafkaProperties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
        JsonDeserializer.class.getName());

    kafkaProperties.put(JsonDeserializer.TRUSTED_PACKAGES, "com.nttbank.microservices.*");

    return new DefaultKafkaConsumerFactory<>(kafkaProperties);
  }


  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, GenericEvent<? extends GenericEvent>>
  kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, GenericEvent<? extends GenericEvent>> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }

  @KafkaListener(topics = "account-service-management-v1")
  public void listenTopic(GenericEvent<? extends GenericEvent> obj) {

    if (obj instanceof CreateBankAccountEvent bankAccountEvent) {
      log.info("Save the new wallet asociated to the user");
      bankAccountRepo.save(BankAccount.builder()
              .accountType("saving")
              .customerId(UUID.randomUUID().toString())
              .balance(BigDecimal.ZERO)
              .maxMonthlyTrans(5000)
              .transactionFee(BigDecimal.ONE)
              .build())
          .doOnSuccess(wallet -> kafkaUtil.sendMessage(CreateWalletEvent.builder()
              .accountId(wallet.getId())
              .documentId(bankAccountEvent.getUsername())
              .email(bankAccountEvent.getEmail())
              .phoneNumber(bankAccountEvent.getPhoneNumber())
              .imei(bankAccountEvent.getImei())
              .status("active")
              .build()))
          .subscribe();
    }

    if (obj instanceof WalletTransferEvent transfer) {
      log.info("Executing the transfer between wallets");
      bankAccountService.transfer(transfer.getSenderAccountId(), transfer.getReceiverAccountId(),
              transfer.getAmount())
          .subscribe(
              response -> {
                log.info(" Yanki Wallet Transfer succeeded");
                kafkaUtil.sendMessage(WalletTransactionEvent.builder()
                    .transactionId(transfer.getTransactionId())
                    .senderAccountId(transfer.getSenderAccountId())
                    .senderPhoneNumber(transfer.getSenderPhoneNumber())
                    .receiverAccountId(transfer.getReceiverAccountId())
                    .receiverPhoneNumber(transfer.getReceiverPhoneNumber())
                    .senderBalanceUpdated(response.getBalanceAfterMovement())
                    .amount(transfer.getAmount())
                    .status("completed")
                    .build());
              },
              error -> {
                log.error(" Yanki Wallet Transfer failed");
                kafkaUtil.sendMessage(WalletTransactionEvent.builder()
                    .transactionId(transfer.getTransactionId())
                    .senderAccountId(transfer.getSenderAccountId())
                    .senderPhoneNumber(transfer.getSenderPhoneNumber())
                    .status("error")
                    .description(error.getMessage())
                    .build());
              }
          );
    }


  }

}
