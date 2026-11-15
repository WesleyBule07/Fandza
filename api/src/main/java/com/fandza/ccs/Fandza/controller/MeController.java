package com.fandza.ccs.Fandza.controller;

import com.fandza.ccs.Fandza.dtos.auth.MeAccountResponse;
import com.fandza.ccs.Fandza.dtos.auth.PatchMeRequest;
import com.fandza.ccs.Fandza.dtos.auth.ProfileTypeRequest;
import com.fandza.ccs.Fandza.entity.User;
import com.fandza.ccs.Fandza.enums.UserType;
import com.fandza.ccs.Fandza.service.MeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
@Tag(name = "Account", description = "Local account of the authenticated user")
public class MeController {

    private final MeService meService;

    public MeController(MeService meService) {
        this.meService = meService;
    }

    @GetMapping
    @Operation(summary = "Get the local account state, syncing from the Keycloak identity",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Current account")
    @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    public MeAccountResponse me(@AuthenticationPrincipal Jwt principal) {
        return MeService.toResponse(meService.sync(principal));
    }

    @PatchMapping
    @Operation(summary = "Update editable account fields", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Updated account")
    public MeAccountResponse patchMe(@AuthenticationPrincipal Jwt principal,
            @Valid @RequestBody PatchMeRequest request) {
        return MeService.toResponse(meService.updateFullName(principal, request.fullName()));
    }

    @PutMapping("/profile-type")
    @Operation(summary = "Choose or change the account profile",
            description = "Only allowed while the contact is unverified and the account is pending (assumption A5).",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Profile updated")
    @ApiResponse(responseCode = "422", description = "Profile is already set or account is verified")
    public ResponseEntity<MeAccountResponse> changeProfileType(@AuthenticationPrincipal Jwt principal,
            @Valid @RequestBody ProfileTypeRequest request) {
        UserType mapped = switch (request.profileType()) {
            case LEGAL_GUARDIAN -> UserType.LEGAL_GUARDIAN;
            case DRIVER -> UserType.DRIVER;
        };
        User user = meService.changeProfileType(principal, mapped);
        return ResponseEntity.ok(MeService.toResponse(user));
    }
}
