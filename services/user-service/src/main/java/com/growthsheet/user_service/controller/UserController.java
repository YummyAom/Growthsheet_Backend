package com.growthsheet.user_service.controller;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.growthsheet.user_service.dto.requests.RegistorSellerRequest;
import com.growthsheet.user_service.dto.requests.UpdatePhotoRequestDTO;
import com.growthsheet.user_service.dto.requests.UpdateUserRoleRequest;
import com.growthsheet.user_service.dto.requests.UserUpdateProfileRequestDTO;
import com.growthsheet.user_service.dto.response.UserProfileResponseDTO;
import com.growthsheet.user_service.entity.User;
import com.growthsheet.user_service.entity.UserRole;
import com.growthsheet.user_service.service.FileService;
import com.growthsheet.user_service.service.UserService;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final FileService fileService;

    public UserController(UserService userService,
            FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @GetMapping("/")
    public String hello() {
        return "hello user1";
    }

    @PostMapping(value = "/registorSeller", consumes = "multipart/form-data")
    public String registorSeller(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestPart("data") RegistorSellerRequest seller,
            @RequestPart("studentCardImage") MultipartFile studentCardImage,
            @RequestPart("selfieWithCardImage") MultipartFile selfieWithCardImage) {

        return userService.createSeller(
                userId,
                seller,
                studentCardImage,
                selfieWithCardImage);
    }

    @PostMapping("/approve-seller/{userId}")
    public String approveSeller(@PathVariable UUID userId) {
        return userService.approveSeller(userId);
    }

    @GetMapping("/me")
    public UserProfileResponseDTO getProfile(
            @RequestHeader("X-USER-ID") UUID userId) {

        return new UserProfileResponseDTO(userService.getProfile(userId));
    }

    @PutMapping("/me")
    public String updateProfile(
            @RequestHeader("X-USER-ID") UUID userId,
            @Valid @RequestBody UserUpdateProfileRequestDTO request) {

        userService.updateProfile(userId, request);
        return "Profile updated";
    }

    @PutMapping(value = "/me/photo", consumes = "multipart/form-data")
    public String updatePhoto(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestPart("image") MultipartFile image) {

        String imageUrl = fileService.uploadImage(image);
        userService.updatePhoto(userId, imageUrl);

        return "Photo updated";
    }

    @GetMapping("/{id}")
    public UserProfileResponseDTO getUserById(@PathVariable UUID id) {
        return new UserProfileResponseDTO(userService.getProfile(id));
    }

    @GetMapping("/page-status")
    public String getSellerPageStatus(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestHeader("X-USER-ROLE") String role) {

        return userService.getSellerPageStatus(userId, role);
    }

    @PutMapping("/{userId}/role")
    public String updateUserRole(
            @PathVariable UUID userId,
            @RequestBody UpdateUserRoleRequest request) {

        userService.updateUserRole(userId, request.getRole());
        return "User role updated";
    }

    // ===== ดึงข้อมูลธนาคารของ seller (สำหรับ internal service call) =====
    @GetMapping("/{userId}/bank-info")
    public java.util.Map<String, String> getSellerBankInfo(@PathVariable UUID userId) {
        return userService.getSellerBankInfo(userId);
    }
}
