package com.fandza.app.Fandza.dtos.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fandza.app.Fandza.enums.UserType;
import java.time.OffsetDateTime;

public record LoginResponse(
        String token,
        String tokenType,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Africa/Maputo")
        OffsetDateTime expiresAt,
        String email,
        UserType userType
) {
}
