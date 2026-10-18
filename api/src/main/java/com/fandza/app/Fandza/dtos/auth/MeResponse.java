package com.fandza.app.Fandza.dtos.auth;

import com.fandza.app.Fandza.enums.UserType;
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
