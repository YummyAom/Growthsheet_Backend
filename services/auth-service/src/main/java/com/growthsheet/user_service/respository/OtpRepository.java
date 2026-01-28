package com.growthsheet.user_service.respository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.user_service.entity.OtpToken;

public interface OtpRepository extends JpaRepository<OtpToken, UUID> {
    Optional<OtpToken> findByEmailAndOtp(String email, String otp);
    void deleteByEmail(String email);
}
