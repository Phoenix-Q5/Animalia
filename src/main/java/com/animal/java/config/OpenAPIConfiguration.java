package com.animal.java.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI exposeAPI(){
        return new OpenAPI().info(new Info().title("The Animalia").description("API built for Animals").version("v1")
                .license(new License().name("Apache License Version 2.0").url("http://phoenixdrone.net"))).externalDocs(new ExternalDocumentation()
                .description("Animalia").url("https://phoenixaerodrone.wordpress.com"));
    }
}
