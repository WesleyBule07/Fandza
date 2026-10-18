package com.fandza.app.Fandza.controller;

import com.fandza.app.Fandza.dtos.driver.DriverRequest;
import com.fandza.app.Fandza.dtos.driver.DriverResponse;
import com.fandza.app.Fandza.dtos.driver.DriverStatusRequest;
import com.fandza.app.Fandza.service.DriverService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/drivers")
@Tag(name = "Drivers", description = "Driver onboarding and account management")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a driver",
            description = "Public onboarding endpoint. The driver is created with PENDING status.")
    @ApiResponse(responseCode = "201", description = "Driver created with PENDING status")
    @ApiResponse(responseCode = "400", description = "Invalid payload")
    @ApiResponse(responseCode = "409", description = "Email already in use")
    public DriverResponse create(@Valid @RequestBody DriverRequest request) {
        return driverService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a driver by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Driver found")
    @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public DriverResponse getById(@PathVariable UUID id) {
        return driverService.getById(id);
    }

    @GetMapping
    @Operation(summary = "List drivers (paginated)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Page of drivers")
    @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    public Page<DriverResponse> findAll(Pageable pageable) {
        return driverService.findAll(pageable);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update a driver account status",
            description = "Applies a validated status transition (PENDING/ACTIVE/SUSPENDED).",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Status updated")
    @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    @ApiResponse(responseCode = "422", description = "Invalid status transition")
    public DriverResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody DriverStatusRequest request) {
        return driverService.updateStatus(id, request.status());
    }
}
