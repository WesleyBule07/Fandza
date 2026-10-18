package com.fandza.app.Fandza.dtos.guardian;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(name = "GuardianResponse", description = "Legal guardian account representation")
public record GuardianResponse(
        @Schema(description = "Legal guardian identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Legal guardian full name", example = "Maria Sitoe")
        String fullName,
        @Schema(description = "Unique email address", example = "maria@fandza.co.mz")
        String email,
        @Schema(description = "Contact phone number", example = "+258840000000")
        String phoneNumber,
        @Schema(description = "Account creation timestamp", example = "2026-01-01T09:00:00+02:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Africa/Maputo")
        OffsetDateTime createdAt
) {
}
