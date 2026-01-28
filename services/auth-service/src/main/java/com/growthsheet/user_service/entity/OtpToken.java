package com.growthsheet.user_service.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OtpToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String otp;
    
    @Column(nullable = false)
    private Instant expiresAt;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public UUID getId() {
        return id;
    }

    public String getOtp() {
        return otp;
    }
}
