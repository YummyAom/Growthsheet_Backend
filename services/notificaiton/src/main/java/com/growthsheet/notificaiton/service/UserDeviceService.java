package com.growthsheet.notificaiton.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.growthsheet.notificaiton.entity.DeviceToken;
import com.growthsheet.notificaiton.repository.DeviceTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDeviceService {

    private final DeviceTokenRepository repository;

    public void saveExpoToken(UUID userId, String token, String deviceType) {

        DeviceToken entity = new DeviceToken();

        entity.setUserId(userId);
        entity.setExpoPushToken(token);
        entity.setDeviceType(deviceType);

        repository.save(entity);
    }
}