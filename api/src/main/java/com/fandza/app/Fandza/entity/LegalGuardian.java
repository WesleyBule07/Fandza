package com.fandza.app.Fandza.entity;

import com.fandza.app.Fandza.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "legal_guardian")
public class LegalGuardian extends AppUser {

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    protected LegalGuardian() {
    }

    public LegalGuardian(String fullName, String email, String password) {
        super(fullName, email, password, UserType.LEGAL_GUARDIAN);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
