package com.growthsheet.notificaiton.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.growthsheet.notificaiton.entity.DeviceToken;
import com.growthsheet.notificaiton.repository.DeviceTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpoPushService {

    private final DeviceTokenRepository tokenRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendPush(UUID userId, String title, String message) {

        List<DeviceToken> tokens = tokenRepository.findByUserId(userId);

        for (DeviceToken token : tokens) {

            Map<String, Object> body = Map.of(
                    "to", token.getExpoPushToken(),
                    "title", title,
                    "body", message
            );

            restTemplate.postForObject(
                    "https://exp.host/--/api/v2/push/send",
                    body,
                    String.class);
        }
    }
}