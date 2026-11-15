package com.fandza.ccs.Fandza.repository;

import com.fandza.ccs.Fandza.entity.Driver;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
}
