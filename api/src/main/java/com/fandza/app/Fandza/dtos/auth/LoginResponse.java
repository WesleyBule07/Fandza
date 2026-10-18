package com.fandza.app.Fandza.dtos.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fandza.app.Fandza.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(name = "LoginResponse", description = "Issued JWT access token and its metadata")
public record LoginResponse(
        @Schema(description = "Signed JWT access token")
        String token,
        @Schema(description = "Token type to use in the Authorization header", example = "Bearer")
        String tokenType,
        @Schema(description = "Token expiration timestamp", example = "2026-01-01T17:00:00+02:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Africa/Maputo")
        OffsetDateTime expiresAt,
        @Schema(description = "Authenticated account email", example = "jane@fandza.co.mz")
        String email,
        @Schema(description = "Authenticated user type", example = "DRIVER")
        UserType userType
) {
}
