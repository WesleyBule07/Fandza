package com.fandza.ccs.Fandza.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fandza.ccs.Fandza.dtos.guardian.GuardianRequest;
import com.fandza.ccs.Fandza.dtos.guardian.GuardianResponse;
import com.fandza.ccs.Fandza.entity.LegalGuardian;
import com.fandza.ccs.Fandza.enums.ContactType;
import com.fandza.ccs.Fandza.exception.ContactAlreadyExistsException;
import com.fandza.ccs.Fandza.exception.GuardianNotFoundException;
import com.fandza.ccs.Fandza.exception.InvalidContactException;
import com.fandza.ccs.Fandza.mapper.GuardianMapper;
import com.fandza.ccs.Fandza.repository.LegalGuardianRepository;
import com.fandza.ccs.Fandza.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuardianServiceTest {

    private static final UUID MISSING_ID = UUID.randomUUID();

    @Mock
    private LegalGuardianRepository legalGuardianRepository;

    @Mock
    private UserRepository userRepository;

    private GuardianService guardianService;

    @BeforeEach
    void setUp() {
        guardianService = new GuardianService(legalGuardianRepository, userRepository,
                new GuardianMapper());
    }

    private GuardianRequest validRequest(String contactValue) {
        return new GuardianRequest("Maria Sitoe", ContactType.PHONE, contactValue);
    }

    @Test
    void createShouldNormalizePhoneAndStartPending() {
        when(userRepository.existsByContactValue("+258840000000")).thenReturn(false);
        when(legalGuardianRepository.save(any(LegalGuardian.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GuardianResponse response = guardianService.create(validRequest("840000000"));

        ArgumentCaptor<LegalGuardian> captor = ArgumentCaptor.forClass(LegalGuardian.class);
        verify(legalGuardianRepository).save(captor.capture());
        LegalGuardian saved = captor.getValue();
        assertThat(saved.getContactValue()).isEqualTo("+258840000000");
        assertThat(saved.getContactType()).isEqualTo(ContactType.PHONE);
        assertThat(saved.isContactVerified()).isFalse();
        assertThat(saved.getAccountStatus().name()).isEqualTo("PENDING");
        assertThat(response.contactValue()).isEqualTo("+258840000000");
        assertThat(response.id()).isNull();
    }

    @Test
    void createShouldRejectInvalidPhone() {
        assertThatThrownBy(() -> guardianService.create(
                new GuardianRequest("Maria", ContactType.PHONE, "12")))
                .isInstanceOf(InvalidContactException.class);

        verify(legalGuardianRepository, never()).save(any());
    }

    @Test
    void createShouldRejectDuplicateContact() {
        when(userRepository.existsByContactValue("+258840000000")).thenReturn(true);

        assertThatThrownBy(() -> guardianService.create(validRequest("840000000")))
                .isInstanceOf(ContactAlreadyExistsException.class);

        verify(legalGuardianRepository, never()).save(any());
    }

    @Test
    void getByIdShouldThrowWhenGuardianIsMissing() {
        when(legalGuardianRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guardianService.getById(MISSING_ID))
                .isInstanceOf(GuardianNotFoundException.class);
    }
}
