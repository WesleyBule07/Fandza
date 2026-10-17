package com.fandza.app.Fandza.repository;

import com.fandza.app.Fandza.entity.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);

    Optional<AppUser> findByEmail(String email);
}
