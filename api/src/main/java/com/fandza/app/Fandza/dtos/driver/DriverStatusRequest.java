package com.fandza.app.Fandza.dtos.driver;

import com.fandza.app.Fandza.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record DriverStatusRequest(
        @NotNull(message = "status must not be null")
        AccountStatus status
) {
}
