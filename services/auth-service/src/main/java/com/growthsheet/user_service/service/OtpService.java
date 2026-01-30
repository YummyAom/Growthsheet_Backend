package com.growthsheet.user_service.service;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.growthsheet.user_service.entity.OtpToken;
import com.growthsheet.user_service.respository.OtpRepository;

import jakarta.transaction.Transactional;

@Service
public class OtpService {
    private OtpRepository otpRep;
    // private EmailService emailService;

    public OtpService(
            OtpRepository otpRep,
            EmailService emailService) {
        this.otpRep = otpRep;
        // this.emailService = emailService;
    }

    public void sendOtp(String email) {
        otpRep.deleteByEmail(email);
        String otp = String.valueOf(
                ThreadLocalRandom.current()
                        .nextInt(10000, 99999));
        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setExpiresAt(
                Instant.now().plusSeconds(300));
        otpRep.save(token);
        // emailService.sendEmail(email, otp);
    }
    
    @Transactional
    public String verify(String email, String otp) {

        OtpToken token = otpRep
                .findByEmailAndOtp(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("OTP expired");
        }

        otpRep.deleteByEmail(email);

        return "OTP verified successfully";
    }

}
