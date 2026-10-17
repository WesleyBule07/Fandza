package com.fandza.app.Fandza.dtos.driver;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record DriverRequest(
        @NotBlank(message = "full name must not be blank")
        String fullName,

        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be valid")
        String email,

        @NotBlank(message = "password must not be blank")
        @Size(min = 8, max = 72, message = "password must be between 8 and 72 characters")
        String password,

        @NotBlank(message = "phone number must not be blank")
        String phoneNumber,

        @NotBlank(message = "license number must not be blank")
        String licenseNumber,

        @NotNull(message = "license expiry must not be null")
        @Future(message = "license expiry must be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Africa/Maputo")
        OffsetDateTime licenseExpiry
) {
}
