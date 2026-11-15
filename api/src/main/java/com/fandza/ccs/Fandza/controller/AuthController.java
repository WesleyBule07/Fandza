package com.fandza.ccs.Fandza.controller;

import com.fandza.ccs.Fandza.dtos.auth.MeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Current-user endpoint backed by Keycloak identity")
public class AuthController {

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated identity", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Current identity")
    @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    public MeResponse me(@AuthenticationPrincipal Jwt principal) {
        return new MeResponse(
                principal.getSubject(),
                principal.getClaimAsString("preferred_username"),
                principal.getClaimAsString("email"),
                principal.getClaimAsString("name"));
    }
}
