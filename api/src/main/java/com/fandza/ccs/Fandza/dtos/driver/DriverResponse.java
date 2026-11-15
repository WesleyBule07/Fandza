package com.fandza.ccs.Fandza.dtos.driver;

import com.fandza.ccs.Fandza.enums.AccountStatus;
import com.fandza.ccs.Fandza.enums.ContactType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "DriverResponse", description = "Driver account representation")
public record DriverResponse(
        @Schema(description = "Driver identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Driver full name", example = "Jane Doe")
        String fullName,
        @Schema(description = "Contact channel used for verification", example = "PHONE")
        ContactType contactType,
        @Schema(description = "Normalized contact value", example = "+258840000000")
        String contactValue,
        @Schema(description = "Whether the contact has been verified", example = "false")
        boolean contactVerified,
        @Schema(description = "Driving licence number", example = "L-123")
        String licenseNumber,
        @Schema(description = "Licence expiry date", example = "2030-12-31T00:00:00Z")
        Instant licenseExpiry,
        @Schema(description = "Account status", example = "PENDING")
        AccountStatus accountStatus,
        @Schema(description = "Account creation timestamp (UTC)", example = "2026-01-01T07:00:00Z")
        Instant createdAt
) {
}
