package com.fandza.ccs.Fandza.controller;

import com.fandza.ccs.Fandza.dtos.auth.MeResponse;
import com.fandza.ccs.Fandza.dtos.auth.RegisterRequest;
import com.fandza.ccs.Fandza.dtos.auth.RegisterResponse;
import com.fandza.ccs.Fandza.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Registration and current identity; tokens come from Keycloak")
public class AuthController {

    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create the local account and send a verification code",
            description = "The client authenticates with Keycloak first; this endpoint creates the local account "
                    + "(single profile, assumption A5) and issues the contact verification code.")
    @ApiResponse(responseCode = "201", description = "Account created; verification code sent")
    @ApiResponse(responseCode = "400", description = "Invalid payload or contact")
    @ApiResponse(responseCode = "409", description = "Contact already registered")
    @ApiResponse(responseCode = "429", description = "Too many verification requests")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return registrationService.register(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated identity claims", security = @SecurityRequirement(name = "bearerAuth"))
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
