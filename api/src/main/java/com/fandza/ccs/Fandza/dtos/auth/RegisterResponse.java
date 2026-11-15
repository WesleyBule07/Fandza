package com.fandza.ccs.Fandza.dtos.auth;

import com.fandza.ccs.Fandza.enums.AccountStatus;
import com.fandza.ccs.Fandza.enums.ContactType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "RegisterResponse", description = "Locally registered account awaiting contact verification")
public record RegisterResponse(
        @Schema(description = "Local account identifier")
        UUID id,
        @Schema(description = "Contact channel used for verification", example = "PHONE")
        ContactType contactType,
        @Schema(description = "Normalized contact value", example = "+258840000000")
        String contactValue,
        @Schema(description = "Whether the contact has been verified", example = "false")
        boolean contactVerified,
        @Schema(description = "Account status", example = "PENDING")
        AccountStatus accountStatus,
        @Schema(description = "Account creation timestamp (UTC)", example = "2026-01-01T07:00:00Z")
        Instant createdAt
) {
}
