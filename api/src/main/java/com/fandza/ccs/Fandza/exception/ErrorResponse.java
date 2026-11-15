package com.fandza.ccs.Fandza.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(name = "ErrorResponse", description = "Standard error payload returned by the API")
public record ErrorResponse(
        @Schema(description = "Timestamp of the error (UTC)", example = "2026-01-01T07:00:00Z")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "HTTP status reason", example = "Bad Request")
        String error,
        @Schema(description = "Human readable error message", example = "Validation failed")
        String message,
        @Schema(description = "Request URI that triggered the error", example = "/api/v1/drivers")
        String path,
        @Schema(description = "Field-level validation errors, when applicable")
        Map<String, String> fieldErrors
) {
}
