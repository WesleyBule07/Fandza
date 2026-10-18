package com.fandza.app.Fandza.dtos.driver;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fandza.app.Fandza.enums.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(name = "DriverResponse", description = "Driver account representation")
public record DriverResponse(
        @Schema(description = "Driver identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Driver full name", example = "Jane Doe")
        String fullName,
        @Schema(description = "Unique email address", example = "jane@fandza.co.mz")
        String email,
        @Schema(description = "Contact phone number", example = "+258840000000")
        String phoneNumber,
        @Schema(description = "Driving licence number", example = "L-123")
        String licenseNumber,
        @Schema(description = "Licence expiry date", example = "2030-12-31T00:00:00+02:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Africa/Maputo")
        OffsetDateTime licenseExpiry,
        @Schema(description = "Account status", example = "PENDING")
        AccountStatus accountStatus,
        @Schema(description = "Account creation timestamp", example = "2026-01-01T09:00:00+02:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Africa/Maputo")
        OffsetDateTime createdAt
) {
}
