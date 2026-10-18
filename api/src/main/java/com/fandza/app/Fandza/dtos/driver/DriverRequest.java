package com.fandza.app.Fandza.dtos.driver;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

@Schema(name = "DriverRequest", description = "Payload to register a new driver")
public record DriverRequest(
        @Schema(description = "Driver full name", example = "Jane Doe")
        @NotBlank(message = "full name must not be blank")
        String fullName,

        @Schema(description = "Unique email address", example = "jane@fandza.co.mz")
        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be valid")
        String email,

        @Schema(description = "Password, between 8 and 72 characters", example = "secret123")
        @NotBlank(message = "password must not be blank")
        @Size(min = 8, max = 72, message = "password must be between 8 and 72 characters")
        String password,

        @Schema(description = "Contact phone number", example = "+258840000000")
        @NotBlank(message = "phone number must not be blank")
        String phoneNumber,

        @Schema(description = "Driving licence number", example = "L-123")
        @NotBlank(message = "license number must not be blank")
        String licenseNumber,

        @Schema(description = "Licence expiry date in the future (ISO-8601 with offset)",
                example = "2030-12-31T00:00:00+02:00")
        @NotNull(message = "license expiry must not be null")
        @Future(message = "license expiry must be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Africa/Maputo")
        OffsetDateTime licenseExpiry
) {
}
