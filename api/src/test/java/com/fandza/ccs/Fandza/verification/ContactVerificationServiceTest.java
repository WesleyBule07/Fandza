package com.fandza.ccs.Fandza.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fandza.ccs.Fandza.entity.ContactVerification;
import com.fandza.ccs.Fandza.exception.InvalidVerificationCodeException;
import com.fandza.ccs.Fandza.exception.TooManyRequestsException;
import com.fandza.ccs.Fandza.repository.ContactVerificationRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ContactVerificationServiceTest {

    private static final String PHONE = "+258840000000";
    private static final String PURPOSE = "CONTACT_VERIFICATION";

    @Mock
    private ContactVerificationRepository repository;

    private ContactVerificationService service;

    @BeforeEach
    void setUp() {
        service = new ContactVerificationService(repository, new VerificationProperties(
                "test-secret", 4, 600, 5, 60, 5));
    }

    @Test
    void issueShouldStoreHashedFourDigitCode() {
        when(repository.findFirstByDestinationAndPurposeOrderByLastSentAtDesc(PHONE, PURPOSE))
                .thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.issue(PHONE, PURPOSE);

        ArgumentCaptor<ContactVerification> captor = ArgumentCaptor.forClass(ContactVerification.class);
        verify(repository).save(captor.capture());
        ContactVerification saved = captor.getValue();
        assertThat(saved.getCodeHash()).hasSize(64);
        assertThat(saved.getCodeHash()).doesNotMatch("\\d{4}");
        assertThat(saved.getDestination()).isEqualTo(PHONE);
        assertThat(saved.getExpiresAt()).isAfter(saved.getLastSentAt());
    }

    @Test
    void issueShouldThrottleResendWithinInterval() {
        ContactVerification recent = new ContactVerification(PHONE, PURPOSE, "hash",
                Instant.now().minusSeconds(10));
        when(repository.findFirstByDestinationAndPurposeOrderByLastSentAtDesc(PHONE, PURPOSE))
                .thenReturn(Optional.of(recent));

        assertThatThrownBy(() -> service.issue(PHONE, PURPOSE))
                .isInstanceOf(TooManyRequestsException.class)
                .hasMessageContaining("wait");
    }

    @Test
    void issueShouldThrottleExcessiveSendsPerHour() {
        ContactVerification older = new ContactVerification(PHONE, PURPOSE, "hash",
                Instant.now().minusSeconds(120));
        when(repository.findFirstByDestinationAndPurposeOrderByLastSentAtDesc(PHONE, PURPOSE))
                .thenReturn(Optional.of(older));
        when(repository.countByDestinationAndLastSentAtAfter(anyString(), any(Instant.class)))
                .thenReturn(5L);

        assertThatThrownBy(() -> service.issue(PHONE, PURPOSE))
                .isInstanceOf(TooManyRequestsException.class)
                .hasMessageContaining("Too many codes");
    }

    @Test
    void confirmShouldAcceptMatchingCodeAndMarkUsedOnce() {
        ContactVerification issued = pendingVerification("4321");
        when(repository.findFirstByDestinationAndPurposeOrderByLastSentAtDesc(PHONE, PURPOSE))
                .thenReturn(Optional.of(issued));

        assertThatCode(() -> service.confirm(PHONE, PURPOSE, "4321"))
                .doesNotThrowAnyException();
        assertThat(issued.getUsedAt()).isNotNull();

        assertThatThrownBy(() -> service.confirm(PHONE, PURPOSE, "4321"))
                .isInstanceOf(InvalidVerificationCodeException.class)
                .hasMessageContaining("already used");
    }

    @Test
    void confirmShouldRejectExpiredCode() {
        ContactVerification issued = pendingVerification("1111");
        ReflectionTestUtils.setField(issued, "expiresAt", Instant.now().minusSeconds(1));
        when(repository.findFirstByDestinationAndPurposeOrderByLastSentAtDesc(PHONE, PURPOSE))
                .thenReturn(Optional.of(issued));

        assertThatThrownBy(() -> service.confirm(PHONE, PURPOSE, "1111"))
                .isInstanceOf(InvalidVerificationCodeException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void confirmShouldCountAttemptsAndLockAfterLimit() {
        ContactVerification issued = pendingVerification("9999");
        when(repository.findFirstByDestinationAndPurposeOrderByLastSentAtDesc(PHONE, PURPOSE))
                .thenReturn(Optional.of(issued));

        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> service.confirm(PHONE, PURPOSE, "0000"))
                    .isInstanceOf(InvalidVerificationCodeException.class);
            assertThat(issued.getAttempts()).isEqualTo(i + 1);
        }

        assertThatThrownBy(() -> service.confirm(PHONE, PURPOSE, "9999"))
                .isInstanceOf(TooManyRequestsException.class)
                .hasMessageContaining("Too many");
    }

    @Test
    void confirmShouldRejectUnknownDestination() {
        when(repository.findFirstByDestinationAndPurposeOrderByLastSentAtDesc(PHONE, PURPOSE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.confirm(PHONE, PURPOSE, "1234"))
                .isInstanceOf(InvalidVerificationCodeException.class)
                .hasMessageContaining("not found");
    }

    private ContactVerification pendingVerification(String code) {
        return new ContactVerification(PHONE, PURPOSE, service.hashOf(code), Instant.now());
    }
}
