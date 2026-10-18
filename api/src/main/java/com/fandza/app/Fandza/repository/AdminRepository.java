package com.fandza.app.Fandza.repository;

import com.fandza.app.Fandza.entity.Admin;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
}
