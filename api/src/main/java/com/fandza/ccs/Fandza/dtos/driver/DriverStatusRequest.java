package com.fandza.ccs.Fandza.dtos.driver;

import com.fandza.ccs.Fandza.enums.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "DriverStatusRequest", description = "Requested new status for a driver account")
public record DriverStatusRequest(
        @Schema(description = "Target account status", example = "ACTIVE")
        @NotNull(message = "status must not be null")
        AccountStatus status
) {
}
