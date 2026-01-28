package com.growthsheet.user_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.growthsheet.user_service.dto.requests.LoginRequest;
import com.growthsheet.user_service.dto.requests.RegisterRequest;
import com.growthsheet.user_service.dto.requests.VerifyOtpRequest;
import com.growthsheet.user_service.dto.response.AuthResponse;
import com.growthsheet.user_service.service.AuthService;
import com.growthsheet.user_service.service.OtpService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(
            AuthService authService,
            OtpService otpService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello auth";
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registor(
            @Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/verify-otp")
    public Map<String, String> verifyOtp(@RequestBody VerifyOtpRequest req) {

        authService.verifyOtp(req.email(), req.otp());

        return Map.of(
                "status", "success",
                "message", "OTP verified successfully");
    }
}
