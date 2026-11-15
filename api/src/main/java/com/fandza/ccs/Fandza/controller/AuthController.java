package com.fandza.ccs.Fandza.controller;

import com.fandza.ccs.Fandza.dtos.auth.LoginRequest;
import com.fandza.ccs.Fandza.dtos.auth.LoginResponse;
import com.fandza.ccs.Fandza.dtos.auth.MeResponse;
import com.fandza.ccs.Fandza.entity.AppUser;
import com.fandza.ccs.Fandza.exception.InvalidCredentialsException;
import com.fandza.ccs.Fandza.repository.AppUserRepository;
import com.fandza.ccs.Fandza.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login and current-user endpoints based on JWT")
public class AuthController {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Africa/Maputo");

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            AppUserRepository appUserRepository,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a user",
            description = "Validates credentials and returns a signed JWT access token.")
    @ApiResponse(responseCode = "200", description = "Authenticated; bearer token issued")
    @ApiResponse(responseCode = "400", description = "Invalid payload")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException();
        }
        AppUser user = appUserRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);
        String token = jwtService.generateToken(user);
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(jwtService.extractExpiration(token), BUSINESS_ZONE);
        return new LoginResponse(token, "Bearer", expiresAt, user.getEmail(), user.getUserType());
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Current user")
    @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    public MeResponse me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        return new MeResponse(user.getEmail(), user.getFullName(), user.getUserType());
    }
}
