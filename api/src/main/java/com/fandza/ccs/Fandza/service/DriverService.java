package com.fandza.ccs.Fandza.service;

import com.fandza.ccs.Fandza.dtos.driver.DriverRequest;
import com.fandza.ccs.Fandza.dtos.driver.DriverResponse;
import com.fandza.ccs.Fandza.entity.Driver;
import com.fandza.ccs.Fandza.enums.AccountStatus;
import com.fandza.ccs.Fandza.exception.DriverNotFoundException;
import com.fandza.ccs.Fandza.exception.EmailAlreadyExistsException;
import com.fandza.ccs.Fandza.exception.InvalidStatusTransitionException;
import com.fandza.ccs.Fandza.mapper.DriverMapper;
import com.fandza.ccs.Fandza.repository.AppUserRepository;
import com.fandza.ccs.Fandza.repository.DriverRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DriverService {

    private final DriverRepository driverRepository;
    private final AppUserRepository appUserRepository;
    private final DriverMapper driverMapper;

    public DriverService(
            DriverRepository driverRepository,
            AppUserRepository appUserRepository,
            DriverMapper driverMapper) {
        this.driverRepository = driverRepository;
        this.appUserRepository = appUserRepository;
        this.driverMapper = driverMapper;
    }

    public DriverResponse create(DriverRequest request) {
        if (appUserRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        Driver driver = driverMapper.toEntity(request);
        Driver saved = driverRepository.save(driver);
        return driverMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public DriverResponse getById(UUID id) {
        return driverMapper.toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<DriverResponse> findAll(Pageable pageable) {
        return driverRepository.findAll(pageable).map(driverMapper::toResponse);
    }

    public DriverResponse updateStatus(UUID id, AccountStatus targetStatus) {
        Driver driver = findOrThrow(id);
        AccountStatus current = driver.getAccountStatus();
        if (current == targetStatus || !isAllowedTransition(current, targetStatus)) {
            throw new InvalidStatusTransitionException(current, targetStatus);
        }
        driver.setAccountStatus(targetStatus);
        return driverMapper.toResponse(driver);
    }

    private static boolean isAllowedTransition(AccountStatus from, AccountStatus to) {
        return switch (from) {
            case PENDING -> to == AccountStatus.ACTIVE || to == AccountStatus.SUSPENDED;
            case ACTIVE -> to == AccountStatus.SUSPENDED;
            case SUSPENDED -> to == AccountStatus.ACTIVE;
        };
    }

    private Driver findOrThrow(UUID id) {
        return driverRepository.findById(id).orElseThrow(() -> new DriverNotFoundException(id));
    }
}
