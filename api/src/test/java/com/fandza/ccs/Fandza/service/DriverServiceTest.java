package com.fandza.ccs.Fandza.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fandza.ccs.Fandza.dtos.driver.DriverRequest;
import com.fandza.ccs.Fandza.dtos.driver.DriverResponse;
import com.fandza.ccs.Fandza.entity.Driver;
import com.fandza.ccs.Fandza.enums.AccountStatus;
import com.fandza.ccs.Fandza.enums.ContactType;
import com.fandza.ccs.Fandza.exception.ContactAlreadyExistsException;
import com.fandza.ccs.Fandza.exception.DriverNotFoundException;
import com.fandza.ccs.Fandza.exception.InvalidContactException;
import com.fandza.ccs.Fandza.exception.InvalidStatusTransitionException;
import com.fandza.ccs.Fandza.mapper.DriverMapper;
import com.fandza.ccs.Fandza.repository.DriverRepository;
import com.fandza.ccs.Fandza.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    private static final UUID DRIVER_ID = UUID.randomUUID();
    private static final UUID MISSING_ID = UUID.randomUUID();

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private UserRepository userRepository;

    private DriverService driverService;

    @BeforeEach
    void setUp() {
        driverService = new DriverService(driverRepository, userRepository, new DriverMapper());
    }

    private DriverRequest validRequest(String contactValue) {
        return new DriverRequest(
                "Jane Doe",
                ContactType.EMAIL,
                contactValue,
                "L-123",
                Instant.now().plusSeconds(60 * 60 * 24 * 365));
    }

    @Test
    void createShouldNormalizeContactAndStartAsPending() {
        when(userRepository.existsByContactValue("jane@fandza.co.mz")).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DriverResponse response = driverService.create(validRequest("Jane@Fandza.co.mz"));

        ArgumentCaptor<Driver> captor = ArgumentCaptor.forClass(Driver.class);
        verify(driverRepository).save(captor.capture());
        Driver saved = captor.getValue();
        assertThat(saved.getAccountStatus()).isEqualTo(AccountStatus.PENDING);
        assertThat(saved.isContactVerified()).isFalse();
        assertThat(saved.getContactValue()).isEqualTo("jane@fandza.co.mz");
        assertThat(response.accountStatus()).isEqualTo(AccountStatus.PENDING);
        assertThat(response.contactValue()).isEqualTo("jane@fandza.co.mz");
        assertThat(response.id()).isNull();
    }

    @Test
    void createShouldRejectInvalidPhone() {
        assertThatThrownBy(() -> driverService.create(
                new DriverRequest("Jane", ContactType.PHONE, "12345", "L-123", null)))
                .isInstanceOf(InvalidContactException.class);

        verify(driverRepository, never()).save(any());
    }

    @Test
    void createShouldRejectDuplicateContact() {
        when(userRepository.existsByContactValue("jane@fandza.co.mz")).thenReturn(true);

        assertThatThrownBy(() -> driverService.create(validRequest("jane@fandza.co.mz")))
                .isInstanceOf(ContactAlreadyExistsException.class);

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
        Driver driver = new Driver("Jane Doe", ContactType.EMAIL, "jane@fandza.co.mz");
        when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));

        DriverResponse response = driverService.updateStatus(DRIVER_ID, AccountStatus.ACTIVE);

        assertThat(driver.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(response.accountStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    void updateStatusShouldRejectInvalidTransition() {
        Driver driver = new Driver("Jane Doe", ContactType.EMAIL, "jane@fandza.co.mz");
        driver.setAccountStatus(AccountStatus.ACTIVE);
        when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.updateStatus(DRIVER_ID, AccountStatus.PENDING))
                .isInstanceOf(InvalidStatusTransitionException.class);

        assertThat(driver.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        verify(driverRepository, never()).save(any());
    }

    @Test
    void updateStatusShouldRejectTransitionToSameStatus() {
        Driver driver = new Driver("Jane Doe", ContactType.EMAIL, "jane@fandza.co.mz");
        when(driverRepository.findById(DRIVER_ID)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.updateStatus(DRIVER_ID, AccountStatus.PENDING))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }
}
