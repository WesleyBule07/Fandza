package com.fandza.app.Fandza.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ErrorResponse", description = "Standard error payload returned by the API")
public record ErrorResponse(
        @Schema(description = "Timestamp of the error", example = "2026-01-01T09:00:00+02:00")
        OffsetDateTime timestamp,
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
