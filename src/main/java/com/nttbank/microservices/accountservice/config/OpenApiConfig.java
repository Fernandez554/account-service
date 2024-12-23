package com.nttbank.microservices.accountservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Banking System API")
            .description("API for managing bank accounts.")
            .version("v1.0")
            .contact(new Contact()
                .name("Jesus Fernandez")
                .url("https://google.com")
                .email("jesus.fernandez.malpartida@gmail.com"))
            .license(new License()
                .name("Apache 2.0")
                .url("http://springdoc.org")))
        .externalDocs(new ExternalDocumentation()
            .description("Springdoc OpenAPI Documentation")
            .url("https://springdoc.org/"));
  }

}
