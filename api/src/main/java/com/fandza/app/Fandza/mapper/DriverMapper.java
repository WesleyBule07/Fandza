package com.fandza.app.Fandza.mapper;

import com.fandza.app.Fandza.dtos.driver.DriverRequest;
import com.fandza.app.Fandza.dtos.driver.DriverResponse;
import com.fandza.app.Fandza.entity.Driver;
import com.fandza.app.Fandza.enums.AccountStatus;
import com.fandza.app.Fandza.enums.UserType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    private final PasswordEncoder passwordEncoder;

    public DriverMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Driver toEntity(DriverRequest request) {
        Driver driver = new Driver(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password()));
        driver.setPhoneNumber(request.phoneNumber());
        driver.setLicenseNumber(request.licenseNumber());
        driver.setLicenseExpiry(request.licenseExpiry());
        driver.setUserType(UserType.DRIVER);
        driver.setAccountStatus(AccountStatus.PENDING);
        return driver;
    }

    public DriverResponse toResponse(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getFullName(),
                driver.getEmail(),
                driver.getPhoneNumber(),
                driver.getLicenseNumber(),
                driver.getLicenseExpiry(),
                driver.getAccountStatus(),
                driver.getCreatedAt());
    }
}
