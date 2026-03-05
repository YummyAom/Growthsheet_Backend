package com.growthsheet.payment_service.config.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "notification-service",
    url = "${GATEWAY_SERVICE_URL}"
)
public interface NotificationClient {

    @PostMapping("/notifications")
    void createNotification(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestParam String title,
            @RequestParam String message
    );
}