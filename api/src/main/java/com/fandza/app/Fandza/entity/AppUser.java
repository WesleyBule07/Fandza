package com.fandza.app.Fandza.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fandza.app.Fandza.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "app_user")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AppUser {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Africa/Maputo");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, updatable = false)
    private UserType userType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected AppUser() {
    }

    protected AppUser(String fullName, String email, String password, UserType userType) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now(BUSINESS_ZONE);
        }
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
