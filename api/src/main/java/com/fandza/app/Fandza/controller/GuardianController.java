package com.fandza.app.Fandza.controller;

import com.fandza.app.Fandza.dtos.guardian.GuardianRequest;
import com.fandza.app.Fandza.dtos.guardian.GuardianResponse;
import com.fandza.app.Fandza.service.GuardianService;
import jakarta.validation.Valid;
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
public class GuardianController {

    private final GuardianService guardianService;

    public GuardianController(GuardianService guardianService) {
        this.guardianService = guardianService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuardianResponse create(@Valid @RequestBody GuardianRequest request) {
        return guardianService.create(request);
    }

    @GetMapping("/{id}")
    public GuardianResponse getById(@PathVariable Long id) {
        return guardianService.getById(id);
    }

    @GetMapping
    public Page<GuardianResponse> findAll(Pageable pageable) {
        return guardianService.findAll(pageable);
    }
}
