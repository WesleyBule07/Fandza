package com.fandza.ccs.Fandza.service;

import com.fandza.ccs.Fandza.dtos.auth.MeAccountResponse;
import com.fandza.ccs.Fandza.entity.User;
import com.fandza.ccs.Fandza.enums.UserType;
import com.fandza.ccs.Fandza.exception.InvalidContactException;
import com.fandza.ccs.Fandza.repository.UserRepository;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeService {

    private final UserRepository userRepository;
    private final RegistrationService registrationService;

    public MeService(UserRepository userRepository, RegistrationService registrationService) {
        this.userRepository = userRepository;
        this.registrationService = registrationService;
    }

    @Transactional
    public User sync(Jwt principal) {
        UUID keycloakId = requireKeycloakId(principal);
        String fullName = valueOrDefault(
                principal.getClaimAsString("name"),
                principal.getClaimAsString("preferred_username"));
        String email = principal.getClaimAsString("email");
        return registrationService.syncFromIdentity(keycloakId, fullName, email, profileFromToken(principal));
    }

    @Transactional
    public User updateFullName(Jwt principal, String fullName) {
        User user = sync(principal);
        user.setFullName(fullName.trim());
        return userRepository.save(user);
    }

    @Transactional
    public User changeProfileType(Jwt principal, UserType newType) {
        User user = sync(principal);
        return registrationService.changeProfileType(user, newType);
    }

    public static MeAccountResponse toResponse(User user) {
        return new MeAccountResponse(
                user.getId(),
                user.getKeycloakId(),
                user.getFullName(),
                user.getContactType(),
                user.getContactValue(),
                user.isContactVerified(),
                user.getAccountStatus(),
                user.getUserType());
    }

    private UUID requireKeycloakId(Jwt principal) {
        try {
            return UUID.fromString(principal.getSubject());
        } catch (IllegalArgumentException e) {
            throw new InvalidContactException("Token subject is not a valid identity");
        }
    }

    private String valueOrDefault(String preferred, String fallback) {
        return preferred != null && !preferred.isBlank() ? preferred : fallback;
    }

    private UserType profileFromToken(Jwt principal) {
        if (principal.getClaim("realm_access") instanceof java.util.Map<?, ?> realmAccess
                && realmAccess.get("roles") instanceof java.util.Collection<?> roles) {
            if (roles.contains("fandza-driver")) {
                return UserType.DRIVER;
            }
            if (roles.contains("fandza-guardian")) {
                return UserType.LEGAL_GUARDIAN;
            }
        }
        return UserType.LEGAL_GUARDIAN;
    }
}
