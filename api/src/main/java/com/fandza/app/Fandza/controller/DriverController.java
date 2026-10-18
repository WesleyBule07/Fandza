package com.fandza.app.Fandza.controller;

import com.fandza.app.Fandza.dtos.driver.DriverRequest;
import com.fandza.app.Fandza.dtos.driver.DriverResponse;
import com.fandza.app.Fandza.dtos.driver.DriverStatusRequest;
import com.fandza.app.Fandza.service.DriverService;
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
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse create(@Valid @RequestBody DriverRequest request) {
        return driverService.create(request);
    }

    @GetMapping("/{id}")
    public DriverResponse getById(@PathVariable UUID id) {
        return driverService.getById(id);
    }

    @GetMapping
    public Page<DriverResponse> findAll(Pageable pageable) {
        return driverService.findAll(pageable);
    }

    @PatchMapping("/{id}/status")
    public DriverResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody DriverStatusRequest request) {
        return driverService.updateStatus(id, request.status());
    }
}
