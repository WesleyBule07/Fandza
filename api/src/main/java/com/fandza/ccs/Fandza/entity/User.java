package com.fandza.ccs.Fandza.entity;

import com.fandza.ccs.Fandza.annotation.UuidV7;
import com.fandza.ccs.Fandza.enums.AccountStatus;
import com.fandza.ccs.Fandza.enums.ContactType;
import com.fandza.ccs.Fandza.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_user")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @UuidV7
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false, updatable = false)
    private ContactType contactType;

    @Column(name = "contact_value", nullable = false, unique = true, updatable = false)
    private String contactValue;

    @Column(name = "contact_verified", nullable = false)
    private boolean contactVerified;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, updatable = false)
    private UserType userType;

    @Column(name = "keycloak_id", updatable = false)
    private UUID keycloakId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected User() {
    }

    protected User(String fullName, ContactType contactType, String contactValue, UserType userType) {
        this.fullName = fullName;
        this.contactType = contactType;
        this.contactValue = contactValue;
        this.contactVerified = false;
        this.accountStatus = AccountStatus.PENDING;
        this.userType = userType;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public String getContactValue() {
        return contactValue;
    }

    public boolean isContactVerified() {
        return contactVerified;
    }

    public void setContactVerified(boolean contactVerified) {
        this.contactVerified = contactVerified;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public UserType getUserType() {
        return userType;
    }

    public UUID getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(UUID keycloakId) {
        this.keycloakId = keycloakId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
