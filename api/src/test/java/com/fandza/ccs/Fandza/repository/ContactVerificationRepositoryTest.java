package com.fandza.ccs.Fandza.repository;

import com.fandza.ccs.Fandza.TestcontainersConfiguration;
import com.fandza.ccs.Fandza.entity.ContactVerification;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ContactVerificationRepositoryTest {

    @Autowired
    private ContactVerificationRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnLatestUnsentVerificationForDestination() {
        Instant now = Instant.now();
        repository.save(new ContactVerification("+258840000000", "CONTACT_VERIFICATION", "hash1",
                now.minusSeconds(60)));
        repository.save(new ContactVerification("+258840000000", "CONTACT_VERIFICATION", "hash2", now));

        var latest = repository
                .findFirstByDestinationAndPurposeOrderByLastSentAtDesc("+258840000000", "CONTACT_VERIFICATION");

        assertThat(latest).hasValueSatisfying(v -> {
            assertThat(v.getCodeHash()).isEqualTo("hash2");
            assertThat(v.getId()).isNotNull();
            assertThat(UUID.fromString(v.getId().toString()).version()).isEqualTo(7);
        });
    }

    @Test
    void shouldCountRecentSends() {
        Instant now = Instant.now();
        repository.save(new ContactVerification("+258840000000", "CONTACT_VERIFICATION", "hash1", now));

        long sent = repository.countByDestinationAndLastSentAtAfter("+258840000000",
                now.minusSeconds(120));

        assertThat(sent).isEqualTo(1);
    }
}
