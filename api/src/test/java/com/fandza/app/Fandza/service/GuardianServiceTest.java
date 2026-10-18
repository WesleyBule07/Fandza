package com.fandza.app.Fandza.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fandza.app.Fandza.dtos.guardian.GuardianRequest;
import com.fandza.app.Fandza.dtos.guardian.GuardianResponse;
import com.fandza.app.Fandza.entity.LegalGuardian;
import com.fandza.app.Fandza.exception.EmailAlreadyExistsException;
import com.fandza.app.Fandza.exception.GuardianNotFoundException;
import com.fandza.app.Fandza.mapper.GuardianMapper;
import com.fandza.app.Fandza.repository.AppUserRepository;
import com.fandza.app.Fandza.repository.LegalGuardianRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class GuardianServiceTest {

    private static final UUID MISSING_ID = UUID.randomUUID();

    @Mock
    private LegalGuardianRepository legalGuardianRepository;

    @Mock
    private AppUserRepository appUserRepository;

    private PasswordEncoder passwordEncoder;
    private GuardianService guardianService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        guardianService = new GuardianService(legalGuardianRepository, appUserRepository,
                new GuardianMapper(passwordEncoder));
    }

    private GuardianRequest validRequest(String email) {
        return new GuardianRequest("Maria Sitoe", email, "secret123", "+258840000000");
    }

    @Test
    void createShouldEncodePasswordAndMapFields() {
        when(appUserRepository.existsByEmail("maria@fandza.co.mz")).thenReturn(false);
        when(legalGuardianRepository.save(any(LegalGuardian.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GuardianResponse response = guardianService.create(validRequest("maria@fandza.co.mz"));

        ArgumentCaptor<LegalGuardian> captor = ArgumentCaptor.forClass(LegalGuardian.class);
        verify(legalGuardianRepository).save(captor.capture());
        LegalGuardian saved = captor.getValue();
        assertThat(saved.getPassword()).isNotEqualTo("secret123");
        assertThat(passwordEncoder.matches("secret123", saved.getPassword())).isTrue();
        assertThat(saved.getPhoneNumber()).isEqualTo("+258840000000");
        assertThat(response.fullName()).isEqualTo("Maria Sitoe");
        assertThat(response.email()).isEqualTo("maria@fandza.co.mz");
        assertThat(response.id()).isNull();
    }

    @Test
    void createShouldRejectDuplicateEmail() {
        when(appUserRepository.existsByEmail("maria@fandza.co.mz")).thenReturn(true);

        assertThatThrownBy(() -> guardianService.create(validRequest("maria@fandza.co.mz")))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(legalGuardianRepository, never()).save(any());
    }

    @Test
    void getByIdShouldThrowWhenGuardianIsMissing() {
        when(legalGuardianRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guardianService.getById(MISSING_ID))
                .isInstanceOf(GuardianNotFoundException.class);
    }
}
