package com.simson.shopsphere.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI shopsphereOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ShopSphere E-Commerce Platform REST API")
                        .description("Comprehensive REST API documentation for ShopSphere (ShopSphere) - Capstone Project")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ShopSphere Capstone Project Team")
                                .email("contact@shopsphere.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
