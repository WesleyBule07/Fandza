package com.fandza.ccs.Fandza.repository;

import com.fandza.ccs.Fandza.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByContactValue(String contactValue);

    Optional<User> findByContactValue(String contactValue);

    Optional<User> findByKeycloakId(UUID keycloakId);
}
