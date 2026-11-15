package com.fandza.ccs.Fandza.repository;

import com.fandza.ccs.Fandza.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByContactValue(String contactValue);

    Optional<User> findByContactValue(String contactValue);

    Optional<User> findByKeycloakId(UUID keycloakId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE app_user SET user_type = :newType WHERE id = :id", nativeQuery = true)
    int updateUserType(@Param("id") UUID id, @Param("newType") String newType);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM legal_guardian WHERE id = :id", nativeQuery = true)
    int deleteGuardianRow(@Param("id") UUID id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM driver WHERE id = :id", nativeQuery = true)
    int deleteDriverRow(@Param("id") UUID id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO legal_guardian (id) VALUES (:id)", nativeQuery = true)
    int insertGuardianRow(@Param("id") UUID id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO driver (id) VALUES (:id)", nativeQuery = true)
    int insertDriverRow(@Param("id") UUID id);
}
