package com.growthsheet.user_service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.growthsheet.user_service.dto.requests.RegistorSellerRequest;
import com.growthsheet.user_service.service.UserService;

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
        return "hello user";
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
}
