package com.fandza.app.Fandza.dtos.driver;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fandza.app.Fandza.enums.AccountStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DriverResponse(
        UUID id,
        String fullName,
        String email,
        String phoneNumber,
        String licenseNumber,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Africa/Maputo")
        OffsetDateTime licenseExpiry,
        AccountStatus accountStatus,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Africa/Maputo")
        OffsetDateTime createdAt
) {
}
