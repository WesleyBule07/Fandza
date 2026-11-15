package com.fandza.ccs.Fandza.entity;

import com.fandza.ccs.Fandza.enums.ContactType;
import com.fandza.ccs.Fandza.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "driver")
public class Driver extends User {

    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    @Column(name = "license_expiry")
    private Instant licenseExpiry;

    protected Driver() {
    }

    public Driver(String fullName, ContactType contactType, String contactValue) {
        super(fullName, contactType, contactValue, UserType.DRIVER);
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Instant getLicenseExpiry() {
        return licenseExpiry;
    }

    public void setLicenseExpiry(Instant licenseExpiry) {
        this.licenseExpiry = licenseExpiry;
    }
}
