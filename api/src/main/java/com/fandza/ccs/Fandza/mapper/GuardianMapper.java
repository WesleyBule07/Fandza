package com.fandza.ccs.Fandza.mapper;

import com.fandza.ccs.Fandza.dtos.guardian.GuardianRequest;
import com.fandza.ccs.Fandza.dtos.guardian.GuardianResponse;
import com.fandza.ccs.Fandza.entity.LegalGuardian;
import com.fandza.ccs.Fandza.util.ContactNormalizer;
import org.springframework.stereotype.Component;

@Component
public class GuardianMapper {

    public LegalGuardian toEntity(GuardianRequest request) {
        return new LegalGuardian(
                request.fullName().trim(),
                request.contactType(),
                ContactNormalizer.normalize(request.contactType(), request.contactValue()));
    }

    public GuardianResponse toResponse(LegalGuardian guardian) {
        return new GuardianResponse(
                guardian.getId(),
                guardian.getFullName(),
                guardian.getContactType(),
                guardian.getContactValue(),
                guardian.isContactVerified(),
                guardian.getAccountStatus(),
                guardian.getCreatedAt());
    }
}
