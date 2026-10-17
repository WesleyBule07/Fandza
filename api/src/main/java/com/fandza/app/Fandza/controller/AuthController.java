package com.fandza.app.Fandza.controller;

import com.fandza.app.Fandza.dtos.auth.LoginRequest;
import com.fandza.app.Fandza.dtos.auth.LoginResponse;
import com.fandza.app.Fandza.dtos.auth.MeResponse;
import com.fandza.app.Fandza.entity.AppUser;
import com.fandza.app.Fandza.exception.InvalidCredentialsException;
import com.fandza.app.Fandza.repository.AppUserRepository;
import com.fandza.app.Fandza.security.JwtService;
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
    public MeResponse me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        return new MeResponse(user.getEmail(), user.getFullName(), user.getUserType());
    }
}
