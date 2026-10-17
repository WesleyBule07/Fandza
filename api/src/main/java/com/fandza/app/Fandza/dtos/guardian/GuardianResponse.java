package com.fandza.app.Fandza.dtos.guardian;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;

public record GuardianResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Africa/Maputo")
        OffsetDateTime createdAt
) {
}
