package com.fandza.app.Fandza.repository;

import com.fandza.app.Fandza.entity.Driver;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
}
