package com.growthsheet.user_service.service;

import java.util.Optional;

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


@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordUtil passwordUtil;
    private final Jwtutil jwtUtil;
    private final OtpService otpService;

    public AuthService(
            UserRepository userRepo,
            PasswordUtil passwordUtil,
            Jwtutil jwtUtil,
            OtpService otpService) {
        this.jwtUtil = jwtUtil;
        this.passwordUtil = passwordUtil;
        this.userRepo = userRepo;
        this.otpService = otpService;
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

    public AuthResponse login(LoginRequest login) {

        User user = userRepo.findByEmail(login.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Email หรือ password ไม่ถูกต้อง"));

        if (!passwordUtil.matches(login.password(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Email หรือ password ไม่ถูกต้อง");
        }

        if (!user.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "กรุณายืนยัน OTP ก่อนเข้าสู่ระบบ");
        }

        return buildResponse(user);
    }

    public void verifyOtp(String email, String otp) {
        otpService.verify(email, otp);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true);
        userRepo.save(user);
    }

}
