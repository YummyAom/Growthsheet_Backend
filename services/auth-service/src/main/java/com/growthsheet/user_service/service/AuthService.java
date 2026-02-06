package com.growthsheet.user_service.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.growthsheet.user_service.dto.requests.LoginRequest;
import com.growthsheet.user_service.dto.requests.RegisterRequest;
import com.growthsheet.user_service.dto.response.AuthResponse;
import com.growthsheet.user_service.entity.User;
import com.growthsheet.user_service.entity.UserRole;
import com.growthsheet.user_service.respository.UserRepository;
import com.growthsheet.user_service.security.Jwtutil;
import com.growthsheet.user_service.security.PasswordUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordUtil passwordUtil;
    private final Jwtutil jwtUtil;
    private final OtpService otpService;
    private final ReactiveRedisTemplate<String, Object> redis;

    // ย้ายมาเป็นระดับ Class Field เพื่อให้ใช้ได้ทุกเมธอด
    private final SecretKey secretKey;
    private final long jwtExpMs;
    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);

    public AuthService(
            UserRepository userRepo,
            PasswordUtil passwordUtil,
            Jwtutil jwtUtil,
            OtpService otpService,
            ReactiveRedisTemplate<String, Object> redis,
            @Value("${JWT_SECRET:default_secret_at_least_32_characters_long}") String jwtSecret,
            @Value("${JWT_EXP_MS:1800000}") long jwtExpMs) {
        this.userRepo = userRepo;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
        this.redis = redis;
        this.jwtExpMs = jwtExpMs;

        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private AuthResponse buildResponse(User user) {
        return new AuthResponse(
                user.getId().toString(),
                user.getEmail(),
                jwtUtil.generate(user));
    }

    public AuthResponse register(RegisterRequest regis) {

        Optional<User> existing = userRepo.findByEmail(regis.email());

        if (!regis.password().equals(regis.secPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "password และ secPassword ไม่ตรงกัน");
        }

        if (existing.isPresent()) {
            User user = existing.get();

            if (user.isEnabled()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Email already exists");

            }

            otpService.sendOtp(user.getEmail());
            return buildResponse(user);
        }

        // case 3: new Email
        User user = new User();
        user.setEmail(regis.email());
        user.setName(regis.username());
        user.setPassword(passwordUtil.hashPassword(regis.password()));
        user.setRole(UserRole.BUYER);
        user.setEnabled(false);
        userRepo.save(user);
        otpService.sendOtp(user.getEmail());

        return buildResponse(user);
    }

    public Mono<Map<String, Object>> login(LoginRequest login) {
        User user = userRepo.findByEmail(login.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Email หรือ password ไม่ถูกต้อง"));

        if (!passwordUtil.matches(login.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email หรือ password ไม่ถูกต้อง");
        }

        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "กรุณายืนยัน OTP ก่อนเข้าสู่ระบบ");
        }

        String accessToken = UUID.randomUUID().toString();
        String redisKey = "access_token:" + accessToken;

        // ใช้ this.secretKey และ this.jwtExpMs ได้เลย
        String sessionToken = Jwts.builder()
                .subject(user.getId().toString())
                .claim("user_id", user.getId().toString())
                .claim("user_name", user.getName())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("user_photo_url", user.getUserPhotoUrl() != null ? user.getUserPhotoUrl() : "")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + this.jwtExpMs))
                .signWith(this.secretKey)
                .compact();

        String refreshToken = UUID.randomUUID().toString();
        String refreshKey = "refresh_token:" + refreshToken;

        Map<String, Object> session = Map.of(
                "user_id", user.getId().toString(),
                "user_name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "user_photo_url", user.getUserPhotoUrl() != null ? user.getUserPhotoUrl() : "",
                "status", "ACTIVE");

        return redis.opsForHash()
                .putAll(redisKey, session)
                .then(redis.expire(redisKey, TOKEN_TTL))
                .then(redis.opsForValue().set(refreshKey, user.getEmail(), Duration.ofDays(7)))
                .thenReturn(Map.of(
                        "access_token", accessToken,
                        "token_type", "bearer",
                        "expires_in", TOKEN_TTL.getSeconds(),
                        "session_token", sessionToken,
                        "refresh_token", refreshToken));
    }

    public void verifyOtp(String email, String otp) {
        otpService.verify(email, otp);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true);
        userRepo.save(user);
    }

    public Mono<Map<String, Object>> refresh(String refreshToken) {
        String refreshKey = "refresh_token:" + refreshToken;

        return redis.opsForValue().get(refreshKey)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Refresh token invalid or expired")))
                .flatMap(emailObj -> {
                    String email = emailObj.toString();

                    User user = userRepo.findByEmail(email)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.UNAUTHORIZED, "User not found"));

                    String newAccessToken = UUID.randomUUID().toString();
                    String redisKey = "access_token:" + newAccessToken;

                    // ตอนนี้ใช้ this.secretKey ในเมธอด refresh ได้แล้ว
                    String newSessionToken = Jwts.builder()
                            .subject(user.getId().toString())
                            .claim("user_id", user.getId().toString())
                            .claim("user_name", user.getName())
                            .claim("role", user.getRole().name())
                            .issuedAt(new Date())
                            .expiration(new Date(System.currentTimeMillis() + this.jwtExpMs))
                            .signWith(this.secretKey)
                            .compact();

                    Map<String, Object> session = Map.of(
                            "user_id", user.getId().toString(),
                            "user_name", user.getName(),
                            "email", user.getEmail(),
                            "role", user.getRole().name(),
                            "status", "ACTIVE");

                    return redis.opsForHash()
                            .putAll(redisKey, session)
                            .then(redis.expire(redisKey, TOKEN_TTL))
                            .thenReturn(Map.of(
                                    "access_token", newAccessToken,
                                    "token_type", "bearer",
                                    "expires_in", TOKEN_TTL.getSeconds(),
                                    "session_token", newSessionToken,
                                    "refresh_token", refreshToken));
                });
    }

    public Mono<Void> logout(String accessToken, String refreshToken) {
        String redisKey = "access_token:" + accessToken;
        String refreshKey = "refresh_token:" + refreshToken;

        return redis.delete(redisKey)
                .then(redis.delete(refreshKey))
                .then();
    }
}
