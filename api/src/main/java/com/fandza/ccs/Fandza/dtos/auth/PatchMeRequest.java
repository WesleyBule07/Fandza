package com.fandza.ccs.Fandza.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "PatchMeRequest", description = "Editable account fields")
public record PatchMeRequest(
        @Schema(description = "Full name", example = "Maria Sitoe Macuácua")
        @NotBlank(message = "full name must not be blank")
        String fullName
) {
}
