package com.fandza.ccs.Fandza.entity;

import com.fandza.ccs.Fandza.enums.AccountStatus;
import com.fandza.ccs.Fandza.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "driver")
public class Driver extends AppUser {

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    @Column(name = "license_expiry")
    private OffsetDateTime licenseExpiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;

    protected Driver() {
    }

    public Driver(String fullName, String email, String password) {
        super(fullName, email, password, UserType.DRIVER);
        this.accountStatus = AccountStatus.PENDING;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public OffsetDateTime getLicenseExpiry() {
        return licenseExpiry;
    }

    public void setLicenseExpiry(OffsetDateTime licenseExpiry) {
        this.licenseExpiry = licenseExpiry;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
}
