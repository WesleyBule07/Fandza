package com.fandza.ccs.Fandza.mapper;

import com.fandza.ccs.Fandza.dtos.driver.DriverRequest;
import com.fandza.ccs.Fandza.dtos.driver.DriverResponse;
import com.fandza.ccs.Fandza.entity.Driver;
import com.fandza.ccs.Fandza.enums.AccountStatus;
import com.fandza.ccs.Fandza.util.ContactNormalizer;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public Driver toEntity(DriverRequest request) {
        Driver driver = new Driver(
                request.fullName().trim(),
                request.contactType(),
                ContactNormalizer.normalize(request.contactType(), request.contactValue()));
        driver.setLicenseNumber(request.licenseNumber());
        driver.setLicenseExpiry(request.licenseExpiry());
        driver.setAccountStatus(AccountStatus.PENDING);
        return driver;
    }

    public DriverResponse toResponse(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getFullName(),
                driver.getContactType(),
                driver.getContactValue(),
                driver.isContactVerified(),
                driver.getLicenseNumber(),
                driver.getLicenseExpiry(),
                driver.getAccountStatus(),
                driver.getCreatedAt());
    }
}
