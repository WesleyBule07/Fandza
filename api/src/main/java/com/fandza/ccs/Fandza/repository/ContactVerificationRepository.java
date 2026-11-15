package com.fandza.ccs.Fandza.repository;

import com.fandza.ccs.Fandza.entity.ContactVerification;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactVerificationRepository extends JpaRepository<ContactVerification, UUID> {

    Optional<ContactVerification> findFirstByDestinationAndPurposeOrderByLastSentAtDesc(
            String destination, String purpose);

    long countByDestinationAndLastSentAtAfter(String destination, Instant after);
}
