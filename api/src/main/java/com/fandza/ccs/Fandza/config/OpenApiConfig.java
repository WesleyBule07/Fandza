package com.fandza.ccs.Fandza.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI fandzaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fandza API")
                        .version("v1")
                        .description("""
                                REST API for the Fandza school transport management platform.

                                It supports driver and legal guardian onboarding, plus JWT based
                                authentication. Protected endpoints require a bearer token obtained
                                from `POST /api/v1/auth/login`.
                                """)
                        .contact(new Contact().name("Fandza"))
                        .license(new License().name("Proprietary")))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Send the JWT access token as 'Authorization: Bearer <token>'")));
    }
}
