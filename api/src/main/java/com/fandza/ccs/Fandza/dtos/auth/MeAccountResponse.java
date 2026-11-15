package com.fandza.ccs.Fandza.dtos.auth;

import com.fandza.ccs.Fandza.enums.AccountStatus;
import com.fandza.ccs.Fandza.enums.ContactType;
import com.fandza.ccs.Fandza.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(name = "MeAccountResponse", description = "Local account state of the authenticated user")
public record MeAccountResponse(
        @Schema(description = "Local account identifier")
        UUID id,
        @Schema(description = "Keycloak subject identifier")
        UUID keycloakId,
        @Schema(description = "Full name", example = "Maria Sitoe")
        String fullName,
        @Schema(description = "Contact channel used for verification", example = "PHONE")
        ContactType contactType,
        @Schema(description = "Normalized contact value", example = "+258840000000")
        String contactValue,
        @Schema(description = "Whether the contact has been verified", example = "false")
        boolean contactVerified,
        @Schema(description = "Account status", example = "PENDING")
        AccountStatus accountStatus,
        @Schema(description = "Chosen profile (one per account)", example = "LEGAL_GUARDIAN")
        UserType profileType
) {
}
