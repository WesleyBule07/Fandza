package com.fandza.ccs.Fandza.dtos.auth;

import com.fandza.ccs.Fandza.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MeResponse", description = "Basic profile of the authenticated user")
public record MeResponse(
        @Schema(description = "Account email", example = "jane@fandza.co.mz")
        String email,
        @Schema(description = "Full name", example = "Jane Doe")
        String fullName,
        @Schema(description = "User type", example = "DRIVER")
        UserType userType
) {
}
