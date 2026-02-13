package com.growthsheet.user_service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.growthsheet.user_service.dto.requests.LoginRequest;
import com.growthsheet.user_service.dto.requests.RegisterRequest;
import com.growthsheet.user_service.dto.requests.VerifyOtpRequest;
import com.growthsheet.user_service.dto.response.AuthResponse;
import com.growthsheet.user_service.service.AuthService;
import com.growthsheet.user_service.service.OtpService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

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

    // Spring จะไปหา JWT_SECRET มาใส่ให้เองอัตโนมัติ
    // ถ้าหาไม่เจอ จะใช้ค่าหลังเครื่องหมาย : คือ "Not Found"
    @Value("${JWT_SECRET:Not Found}")
    private String jwtSecret;

    @GetMapping("/env-value")
    public String getEnvByValue() {
        return "ค่าจาก @Value คือ: " + jwtSecret;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registor(
            @Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public Mono<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Map<String, String>>> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {

        String accessToken = authHeader.replace("Bearer ", "");
        String refreshToken = body.get("refresh_token");

        return authService.logout(accessToken, refreshToken)
                .thenReturn(ResponseEntity.ok(Map.of("message", "Logged out successfully")));
    }

    @PostMapping("/refresh")
    public Mono<Map<String, Object>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");
        if (refreshToken == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing refresh_token"));
        }
        return authService.refresh(refreshToken);
    }

    @PostMapping("/verify-otp")
    public Map<String, String> verifyOtp(@RequestBody VerifyOtpRequest req) {

        authService.verifyOtp(req.email(), req.otp());

        return Map.of(
                "status", "success",
                "message", "OTP verified successfully");
    }
}
