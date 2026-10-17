package com.fandza.app.Fandza.repository;

import com.fandza.app.Fandza.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);
}
