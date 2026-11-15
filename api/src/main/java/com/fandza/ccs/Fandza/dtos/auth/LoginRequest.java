package com.fandza.ccs.Fandza.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Credentials to authenticate")
public record LoginRequest(
        @Schema(description = "Account email", example = "jane@fandza.co.mz")
        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be valid")
        String email,

        @Schema(description = "Account password", example = "secret123")
        @NotBlank(message = "password must not be blank")
        String password
) {
}
