package com.fandza.app.Fandza.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fandza.app.Fandza.dtos.driver.DriverRequest;
import com.fandza.app.Fandza.dtos.driver.DriverResponse;
import com.fandza.app.Fandza.entity.Driver;
import com.fandza.app.Fandza.enums.AccountStatus;
import com.fandza.app.Fandza.exception.DriverNotFoundException;
import com.fandza.app.Fandza.exception.EmailAlreadyExistsException;
import com.fandza.app.Fandza.exception.InvalidStatusTransitionException;
import com.fandza.app.Fandza.mapper.DriverMapper;
import com.fandza.app.Fandza.repository.AppUserRepository;
import com.fandza.app.Fandza.repository.DriverRepository;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
class DriverServiceTest {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Africa/Maputo");
    private static final UUID DRIVER_ID = UUID.randomUUID();
    private static final UUID MISSING_ID = UUID.randomUUID();

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private AppUserRepository appUserRepository;

    private PasswordEncoder passwordEncoder;
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        driverService = new DriverService(driverRepository, appUserRepository,
                new DriverMapper(passwordEncoder));
    }

    private DriverRequest validRequest(String email) {
        return new DriverRequest(
                "Jane Doe",
                email,
                "secret123",
                "+258840000000",
                "L-123",
                OffsetDateTime.now(BUSINESS_ZONE).plusYears(1));
    }

    @Test
    void createShouldStartDriverAsPendingWithEncodedPassword() {
        when(appUserRepository.existsByEmail("jane@fandza.co.mz")).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DriverResponse response = driverService.create(validRequest("jane@fandza.co.mz"));

        ArgumentCaptor<Driver> captor = ArgumentCaptor.forClass(Driver.class);
        verify(driverRepository).save(captor.capture());
        Driver saved = captor.getValue();
        assertThat(saved.getAccountStatus()).isEqualTo(AccountStatus.PENDING);
        assertThat(saved.getPassword()).isNotEqualTo("secret123");
        assertThat(passwordEncoder.matches("secret123", saved.getPassword())).isTrue();
        assertThat(response.accountStatus()).isEqualTo(AccountStatus.PENDING);
        assertThat(response.email()).isEqualTo("jane@fandza.co.mz");
        assertThat(response.id()).isNull();
    }

    @Test
    void createShouldRejectDuplicateEmail() {
        when(appUserRepository.existsByEmail("jane@fandza.co.mz")).thenReturn(true);

        assertThatThrownBy(() -> driverService.create(validRequest("jane@fandza.co.mz")))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(driverRepository, never()).save(any());
    }

    @Test
    void getByIdShouldThrowWhenDriverIsMissing() {
        when(driverRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.getById(MISSING_ID))
                .isInstanceOf(DriverNotFoundException.class);
    }

    @Test
    void updateStatusShouldActivatePendingDriver() {
        Driver driver = new Driver("Jane Doe", "jane@fandza.co.mz", "hash");
        when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));

        DriverResponse response = driverService.updateStatus(DRIVER_ID, AccountStatus.ACTIVE);

        assertThat(driver.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(response.accountStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    void updateStatusShouldRejectInvalidTransition() {
        Driver driver = new Driver("Jane Doe", "jane@fandza.co.mz", "hash");
        driver.setAccountStatus(AccountStatus.ACTIVE);
        when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.updateStatus(DRIVER_ID, AccountStatus.PENDING))
                .isInstanceOf(InvalidStatusTransitionException.class);

        assertThat(driver.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        verify(driverRepository, never()).save(any());
    }

    @Test
    void updateStatusShouldRejectTransitionToSameStatus() {
        Driver driver = new Driver("Jane Doe", "jane@fandza.co.mz", "hash");
        when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.updateStatus(DRIVER_ID, AccountStatus.PENDING))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }
}
