package com.fandza.ccs.Fandza.dtos.guardian;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "GuardianRequest", description = "Payload to register a new legal guardian")
public record GuardianRequest(
        @Schema(description = "Legal guardian full name", example = "Maria Sitoe")
        @NotBlank(message = "full name must not be blank")
        String fullName,

        @Schema(description = "Unique email address", example = "maria@fandza.co.mz")
        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be valid")
        String email,

        @Schema(description = "Password, between 8 and 72 characters", example = "secret123")
        @NotBlank(message = "password must not be blank")
        @Size(min = 8, max = 72, message = "password must be between 8 and 72 characters")
        String password,

        @Schema(description = "Contact phone number", example = "+258840000000")
        @NotBlank(message = "phone number must not be blank")
        String phoneNumber
) {
}
