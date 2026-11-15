package com.fandza.ccs.Fandza.repository;

import com.fandza.ccs.Fandza.entity.Admin;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
}
