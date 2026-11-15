package com.fandza.ccs.Fandza.repository;

import com.fandza.ccs.Fandza.entity.LegalGuardian;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalGuardianRepository extends JpaRepository<LegalGuardian, UUID> {
}
