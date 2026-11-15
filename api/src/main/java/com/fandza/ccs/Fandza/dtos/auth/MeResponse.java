package com.fandza.ccs.Fandza.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MeResponse", description = "Identity claims of the authenticated user")
public record MeResponse(
        @Schema(description = "Keycloak subject identifier", example = "8b6d2f7e-1c34-4b7a-9f0e-2d5a6b7c8d9e")
        String keycloakId,
        @Schema(description = "Preferred username", example = "jane")
        String username,
        @Schema(description = "Account email", example = "jane@fandza.co.mz")
        String email,
        @Schema(description = "Full name", example = "Jane Doe")
        String fullName
) {
}
