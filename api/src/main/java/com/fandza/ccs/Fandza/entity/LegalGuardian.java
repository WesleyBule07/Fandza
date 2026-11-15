package com.fandza.ccs.Fandza.entity;

import com.fandza.ccs.Fandza.enums.ContactType;
import com.fandza.ccs.Fandza.enums.UserType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "legal_guardian")
public class LegalGuardian extends User {

    protected LegalGuardian() {
    }

    public LegalGuardian(String fullName, ContactType contactType, String contactValue) {
        super(fullName, contactType, contactValue, UserType.LEGAL_GUARDIAN);
    }
}
