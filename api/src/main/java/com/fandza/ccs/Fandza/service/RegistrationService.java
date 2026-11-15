package com.fandza.ccs.Fandza.service;

import com.fandza.ccs.Fandza.dtos.auth.RegisterRequest;
import com.fandza.ccs.Fandza.dtos.auth.RegisterResponse;
import com.fandza.ccs.Fandza.entity.Driver;
import com.fandza.ccs.Fandza.entity.LegalGuardian;
import com.fandza.ccs.Fandza.entity.User;
import com.fandza.ccs.Fandza.exception.InvalidContactException;
import com.fandza.ccs.Fandza.repository.UserRepository;
import com.fandza.ccs.Fandza.util.ContactNormalizer;
import com.fandza.ccs.Fandza.verification.ContactVerificationService;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
    private static final String PURPOSE = "CONTACT_VERIFICATION";

    private final UserRepository userRepository;
    private final ContactVerificationService verificationService;

    public RegistrationService(UserRepository userRepository,
            @Lazy ContactVerificationService verificationService) {
        this.userRepository = userRepository;
        this.verificationService = verificationService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String normalized = ContactNormalizer.normalize(request.contactType(), request.contactValue());
        if (userRepository.existsByContactValue(normalized)) {
            throw new com.fandza.ccs.Fandza.exception.ContactAlreadyExistsException(normalized);
        }
        if (request.fullName() == null || request.fullName().isBlank()) {
            throw new InvalidContactException("Full name must not be blank");
        }

        User user = switch (request.profileType()) {
            case LEGAL_GUARDIAN -> new LegalGuardian(request.fullName().trim(), request.contactType(), normalized);
            case DRIVER -> new Driver(request.fullName().trim(), request.contactType(), normalized);
        };
        User saved = userRepository.save(user);

        verificationService.issue(normalized, PURPOSE);
        return new RegisterResponse(
                saved.getId(),
                saved.getContactType(),
                saved.getContactValue(),
                saved.isContactVerified(),
                saved.getAccountStatus(),
                Instant.now());
    }

    @Transactional
    public User syncFromIdentity(UUID keycloakId, String fullName, String email,
            com.fandza.ccs.Fandza.enums.UserType profileType) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    String normalized = ContactNormalizer.normalize(
                            com.fandza.ccs.Fandza.enums.ContactType.EMAIL, email);
                    User existing = userRepository.findByContactValue(normalized).orElse(null);
                    if (existing != null) {
                        existing.setKeycloakId(keycloakId);
                        existing.setFullName(fullName);
                        return userRepository.save(existing);
                    }
                    User provisioned = switch (profileType) {
                        case DRIVER -> new Driver(fullName,
                                com.fandza.ccs.Fandza.enums.ContactType.EMAIL, normalized);
                        case LEGAL_GUARDIAN -> new LegalGuardian(fullName,
                                com.fandza.ccs.Fandza.enums.ContactType.EMAIL, normalized);
                    };
                    provisioned.setKeycloakId(keycloakId);
                    log.info("Auto-provisioned local account {} for Keycloak subject {}", provisioned.getId(), keycloakId);
                    return userRepository.save(provisioned);
                });
    }

    @Transactional
    public User changeProfileType(User user, com.fandza.ccs.Fandza.enums.UserType newType) {
        if (user.getUserType() == newType) {
            throw new com.fandza.ccs.Fandza.exception.InvalidProfileChangeException(
                    "Account already uses the requested profile");
        }
        if (user.isContactVerified()
                || user.getAccountStatus() != com.fandza.ccs.Fandza.enums.AccountStatus.PENDING) {
            throw new com.fandza.ccs.Fandza.exception.InvalidProfileChangeException(
                    "Profile can only be changed while the account is unverified and pending");
        }
        userRepository.deleteGuardianRow(user.getId());
        userRepository.deleteDriverRow(user.getId());
        userRepository.updateUserType(user.getId(), newType.name());
        if (newType == com.fandza.ccs.Fandza.enums.UserType.DRIVER) {
            userRepository.insertDriverRow(user.getId());
        } else {
            userRepository.insertGuardianRow(user.getId());
        }
        user = userRepository.findById(user.getId()).orElseThrow();
        return user;
    }

}
