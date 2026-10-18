package com.fandza.app.Fandza.repository;

import com.fandza.app.Fandza.entity.LegalGuardian;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalGuardianRepository extends JpaRepository<LegalGuardian, UUID> {
}
