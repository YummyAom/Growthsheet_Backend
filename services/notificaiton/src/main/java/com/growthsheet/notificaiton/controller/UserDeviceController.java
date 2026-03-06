package com.growthsheet.notificaiton.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.notificaiton.dto.ExpoTokenRequest;
import com.growthsheet.notificaiton.service.UserDeviceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/userdevice")
@RequiredArgsConstructor
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @PostMapping("/expo-token")
    public void saveTokenExpo(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestBody ExpoTokenRequest request) {

        userDeviceService.saveExpoToken(
                userId,
                request.getToken(),
                request.getDeviceType());

    }
}