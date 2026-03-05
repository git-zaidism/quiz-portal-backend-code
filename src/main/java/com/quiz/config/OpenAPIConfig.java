package com.quiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * OpenAPI Configuration for Swagger/OpenAPI Documentation
 * This configuration sets up the API documentation with security schemes for JWT authentication.
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Quiz Portal API")
                        .version("1.0.0")
                        .description("REST API for Quiz Portal - A comprehensive platform for creating, managing, and taking quizzes. " +
                                "This API provides endpoints for user authentication, quiz management, questions, categories, and user management.")
                        .contact(new Contact()
                                .name("Quiz Portal Support")
                                .email("support@quizportal.com")
                                .url("https://quizportal.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
