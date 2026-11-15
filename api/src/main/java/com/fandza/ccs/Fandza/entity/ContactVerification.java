package com.fandza.ccs.Fandza.entity;

import com.fandza.ccs.Fandza.annotation.UuidV7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contact_verification")
public class ContactVerification {

    @Id
    @UuidV7
    private UUID id;

    @Column(nullable = false, length = 255)
    private String destination;

    @Column(nullable = false, length = 64)
    private String purpose;

    @Column(name = "code_hash", nullable = false, length = 255)
    private String codeHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "last_sent_at", nullable = false)
    private Instant lastSentAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ContactVerification() {
    }

    public ContactVerification(String destination, String purpose, String codeHash, Instant lastSentAt) {
        this.destination = destination;
        this.purpose = purpose;
        this.codeHash = codeHash;
        this.lastSentAt = lastSentAt;
        this.expiresAt = lastSentAt.plusSeconds(600);
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public Instant getLastSentAt() {
        return lastSentAt;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(Instant usedAt) {
        this.usedAt = usedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
