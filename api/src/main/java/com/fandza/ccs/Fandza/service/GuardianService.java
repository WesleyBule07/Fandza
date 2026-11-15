package com.fandza.ccs.Fandza.service;

import com.fandza.ccs.Fandza.dtos.guardian.GuardianRequest;
import com.fandza.ccs.Fandza.dtos.guardian.GuardianResponse;
import com.fandza.ccs.Fandza.entity.LegalGuardian;
import com.fandza.ccs.Fandza.exception.ContactAlreadyExistsException;
import com.fandza.ccs.Fandza.exception.GuardianNotFoundException;
import com.fandza.ccs.Fandza.mapper.GuardianMapper;
import com.fandza.ccs.Fandza.repository.UserRepository;
import com.fandza.ccs.Fandza.util.ContactNormalizer;
import com.fandza.ccs.Fandza.repository.LegalGuardianRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GuardianService {

    private final LegalGuardianRepository legalGuardianRepository;
    private final UserRepository userRepository;
    private final GuardianMapper guardianMapper;

    public GuardianService(
            LegalGuardianRepository legalGuardianRepository,
            UserRepository userRepository,
            GuardianMapper guardianMapper) {
        this.legalGuardianRepository = legalGuardianRepository;
        this.userRepository = userRepository;
        this.guardianMapper = guardianMapper;
    }

    public GuardianResponse create(GuardianRequest request) {
        if (userRepository.existsByContactValue(ContactNormalizer.normalize(request.contactType(), request.contactValue()))) {
            throw new ContactAlreadyExistsException(request.contactValue());
        }
        LegalGuardian guardian = guardianMapper.toEntity(request);
        LegalGuardian saved = legalGuardianRepository.save(guardian);
        return guardianMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public GuardianResponse getById(UUID id) {
        return guardianMapper.toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<GuardianResponse> findAll(Pageable pageable) {
        return legalGuardianRepository.findAll(pageable).map(guardianMapper::toResponse);
    }

    private LegalGuardian findOrThrow(UUID id) {
        return legalGuardianRepository.findById(id).orElseThrow(() -> new GuardianNotFoundException(id));
    }
}
