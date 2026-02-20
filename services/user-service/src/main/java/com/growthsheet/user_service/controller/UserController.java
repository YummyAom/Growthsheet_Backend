package com.growthsheet.user_service.controller;

import java.util.UUID;

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
import com.growthsheet.user_service.dto.requests.UserUpdateProfileRequestDTO;
import com.growthsheet.user_service.dto.response.UserProfileResponseDTO;
import com.growthsheet.user_service.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(
            UserService userService) {
        this.userService = userService;
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

    @PutMapping("/me/photo")
    public String updatePhoto(
            @RequestHeader("X-USER-ID") UUID userId,
            @Valid @RequestBody UpdatePhotoRequestDTO request) {

        userService.updatePhoto(userId, request.getPhotoUrl());
        return "Photo updated";
    }


    // @GetMapping("/sellers")
    // public
}
