package com.fandza.ccs.Fandza.dtos.auth;

import com.fandza.ccs.Fandza.dtos.auth.RegisterRequest.ProfileType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ProfileTypeRequest", description = "Chooses or changes the account profile")
public record ProfileTypeRequest(
        @Schema(description = "Profile of the account", example = "DRIVER")
        @NotNull(message = "profile type must not be null")
        ProfileType profileType
) {
}
