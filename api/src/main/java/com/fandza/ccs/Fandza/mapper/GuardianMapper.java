package com.fandza.ccs.Fandza.mapper;

import com.fandza.ccs.Fandza.dtos.guardian.GuardianRequest;
import com.fandza.ccs.Fandza.dtos.guardian.GuardianResponse;
import com.fandza.ccs.Fandza.entity.LegalGuardian;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class GuardianMapper {

    private final PasswordEncoder passwordEncoder;

    public GuardianMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public LegalGuardian toEntity(GuardianRequest request) {
        LegalGuardian guardian = new LegalGuardian(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password()));
        guardian.setPhoneNumber(request.phoneNumber());
        return guardian;
    }

    public GuardianResponse toResponse(LegalGuardian guardian) {
        return new GuardianResponse(
                guardian.getId(),
                guardian.getFullName(),
                guardian.getEmail(),
                guardian.getPhoneNumber(),
                guardian.getCreatedAt());
    }
}
