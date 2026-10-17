package com.fandza.app.Fandza.service;

import com.fandza.app.Fandza.dtos.guardian.GuardianRequest;
import com.fandza.app.Fandza.dtos.guardian.GuardianResponse;
import com.fandza.app.Fandza.entity.LegalGuardian;
import com.fandza.app.Fandza.exception.EmailAlreadyExistsException;
import com.fandza.app.Fandza.exception.GuardianNotFoundException;
import com.fandza.app.Fandza.mapper.GuardianMapper;
import com.fandza.app.Fandza.repository.AppUserRepository;
import com.fandza.app.Fandza.repository.LegalGuardianRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GuardianService {

    private final LegalGuardianRepository legalGuardianRepository;
    private final AppUserRepository appUserRepository;
    private final GuardianMapper guardianMapper;

    public GuardianService(
            LegalGuardianRepository legalGuardianRepository,
            AppUserRepository appUserRepository,
            GuardianMapper guardianMapper) {
        this.legalGuardianRepository = legalGuardianRepository;
        this.appUserRepository = appUserRepository;
        this.guardianMapper = guardianMapper;
    }

    public GuardianResponse create(GuardianRequest request) {
        if (appUserRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        LegalGuardian guardian = guardianMapper.toEntity(request);
        LegalGuardian saved = legalGuardianRepository.save(guardian);
        return guardianMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public GuardianResponse getById(Long id) {
        return guardianMapper.toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<GuardianResponse> findAll(Pageable pageable) {
        return legalGuardianRepository.findAll(pageable).map(guardianMapper::toResponse);
    }

    private LegalGuardian findOrThrow(Long id) {
        return legalGuardianRepository.findById(id).orElseThrow(() -> new GuardianNotFoundException(id));
    }
}
