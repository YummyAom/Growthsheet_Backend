package com.growthsheet.user_service.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.growthsheet.user_service.dto.requests.GoogleLoginRequest;
import com.growthsheet.user_service.dto.requests.ChangePasswordRequest;
import com.growthsheet.user_service.dto.requests.DeleteAccountRequest;
import com.growthsheet.user_service.dto.requests.ForgotPasswordRequest;
import com.growthsheet.user_service.dto.requests.LoginRequest;
import com.growthsheet.user_service.dto.requests.RegisterRequest;
import com.growthsheet.user_service.dto.requests.ResetPasswordRequest;
import com.growthsheet.user_service.dto.requests.VerifyOtpRequest;
import com.growthsheet.user_service.dto.response.AuthResponse;
import com.growthsheet.user_service.service.AuthService;
import com.growthsheet.user_service.service.GoogleService;
import com.growthsheet.user_service.service.OtpService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;
    private GoogleService googleService;
    @Value("${GOOGLE_ID}")
    String GOOGLE_CLIENT_ID;

    public AuthController(
            AuthService authService,
            OtpService otpService,
            GoogleService googleService) {
        this.authService = authService;
        this.googleService = googleService;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello auth";
    }

    @GetMapping("/google")
    public void googleLogin(HttpServletResponse res) throws IOException {

        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + GOOGLE_CLIENT_ID
                + "&redirect_uri=http://localhost:8080/api/auth/google/callback"
                + "&response_type=code"
                + "&scope=openid email profile";

        res.sendRedirect(url);
    }

    @GetMapping("/google/callback")
    public void googleCallback(
            @RequestParam String code,
            HttpServletResponse response) throws IOException {

        String jwt = googleService.handleGoogleLogin(code);

        response.sendRedirect("mymobileapp://auth?token=" + jwt);
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

    @PostMapping("/admin-login")
    public Mono<Map<String, Object>> adminLogin(
            @Valid @RequestBody LoginRequest req) {

        return authService.adminLogin(req);
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

    // ===== เปลี่ยนรหัสผ่าน (ต้อง login) =====
    @PutMapping("/change-password")
    public Map<String, String> changePassword(
            @RequestHeader("X-USER-ID") UUID userId,
            @Valid @RequestBody ChangePasswordRequest req) {

        authService.changePassword(userId, req);

        return Map.of(
                "status", "success",
                "message", "เปลี่ยนรหัสผ่านสำเร็จ");
    }

    // ===== ลืมรหัสผ่าน (ไม่ต้อง login — ส่ง OTP ไป email) =====
    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest req) {

        authService.forgotPassword(req.email());

        return Map.of(
                "status", "success",
                "message", "ส่ง OTP ไปยัง email เรียบร้อยแล้ว");
    }

    // ===== Reset Password (ไม่ต้อง login — ยืนยัน OTP แล้วเปลี่ยนรหัส) =====
    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {

        authService.resetPassword(req);

        return Map.of(
                "status", "success",
                "message", "เปลี่ยนรหัสผ่านสำเร็จ กรุณาเข้าสู่ระบบใหม่");
    }

    @DeleteMapping("/delete-account")
    public Map<String, String> deleteAccount(
            @RequestHeader("X-USER-ID") UUID userId,
            @Valid @RequestBody DeleteAccountRequest req) { // ✅ เพิ่ม body

        authService.deleteAccount(userId, req.password());

        return Map.of(
                "status", "success",
                "message", "ลบบัญชีเรียบร้อยแล้ว");
    }

}
