package com.fandza.ccs.Fandza.dtos.guardian;

import com.fandza.ccs.Fandza.enums.ContactType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "GuardianRequest", description = "Payload to register a new legal guardian")
public record GuardianRequest(
        @Schema(description = "Legal guardian full name", example = "Maria Sitoe")
        @NotBlank(message = "full name must not be blank")
        String fullName,

        @Schema(description = "Contact channel used for verification", example = "PHONE")
        @NotNull(message = "contact type must not be null")
        ContactType contactType,

        @Schema(description = "Raw contact value: phone (+258) or email", example = "840000000")
        @NotBlank(message = "contact value must not be blank")
        String contactValue
) {
}
