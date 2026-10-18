package com.fandza.app.Fandza.controller;

import com.fandza.app.Fandza.dtos.guardian.GuardianRequest;
import com.fandza.app.Fandza.dtos.guardian.GuardianResponse;
import com.fandza.app.Fandza.service.GuardianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guardians")
@Tag(name = "Legal Guardians", description = "Legal guardian (parent or tutor) onboarding")
public class GuardianController {

    private final GuardianService guardianService;

    public GuardianController(GuardianService guardianService) {
        this.guardianService = guardianService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a legal guardian",
            description = "Public onboarding endpoint for parents or tutors.")
    @ApiResponse(responseCode = "201", description = "Legal guardian created")
    @ApiResponse(responseCode = "400", description = "Invalid payload")
    @ApiResponse(responseCode = "409", description = "Email already in use")
    public GuardianResponse create(@Valid @RequestBody GuardianRequest request) {
        return guardianService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a legal guardian by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Legal guardian found")
    @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    @ApiResponse(responseCode = "404", description = "Legal guardian not found")
    public GuardianResponse getById(@PathVariable UUID id) {
        return guardianService.getById(id);
    }

    @GetMapping
    @Operation(summary = "List legal guardians (paginated)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Page of legal guardians")
    @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    public Page<GuardianResponse> findAll(Pageable pageable) {
        return guardianService.findAll(pageable);
    }
}
