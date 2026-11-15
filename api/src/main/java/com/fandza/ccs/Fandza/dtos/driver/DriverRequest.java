package com.fandza.ccs.Fandza.dtos.driver;

import com.fandza.ccs.Fandza.enums.ContactType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Schema(name = "DriverRequest", description = "Payload to register a new driver")
public record DriverRequest(
        @Schema(description = "Driver full name", example = "Jane Doe")
        @NotBlank(message = "full name must not be blank")
        String fullName,

        @Schema(description = "Contact channel used for verification", example = "PHONE")
        @NotNull(message = "contact type must not be null")
        ContactType contactType,

        @Schema(description = "Raw contact value: phone (+258) or email", example = "840000000")
        @NotBlank(message = "contact value must not be blank")
        String contactValue,

        @Schema(description = "Driving licence number", example = "L-123")
        @NotBlank(message = "license number must not be blank")
        String licenseNumber,

        @Schema(description = "Licence expiry date", example = "2030-12-31T00:00:00Z")
        Instant licenseExpiry
) {
}
