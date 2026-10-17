package com.fandza.app.Fandza.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be valid")
        String email,

        @NotBlank(message = "password must not be blank")
        String password
) {
}
