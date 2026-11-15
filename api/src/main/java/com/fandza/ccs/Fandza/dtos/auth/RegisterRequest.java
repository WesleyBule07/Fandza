package com.fandza.ccs.Fandza.dtos.auth;

import com.fandza.ccs.Fandza.enums.ContactType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "RegisterRequest", description = "Creates the local account after the Keycloak identity exists")
public record RegisterRequest(
        @Schema(description = "Profile chosen for the account", example = "LEGAL_GUARDIAN")
        @NotNull(message = "profile type must not be null")
        ProfileType profileType,

        @Schema(description = "Full name", example = "Maria Sitoe")
        @NotBlank(message = "full name must not be blank")
        String fullName,

        @Schema(description = "Contact channel used for verification", example = "PHONE")
        @NotNull(message = "contact type must not be null")
        ContactType contactType,

        @Schema(description = "Raw contact value: phone (+258) or email", example = "840000000")
        @NotBlank(message = "contact value must not be blank")
        String contactValue
) {

    @Schema(name = "ProfileType", description = "Profile of a Fandza account (one per account, A5)")
    public enum ProfileType {
        LEGAL_GUARDIAN,
        DRIVER
    }
}
