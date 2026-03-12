package com.growthsheet.payment_service.config.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.growthsheet.payment_service.dto.NotificationRequest;

@FeignClient(
    name = "notification-service",
    url = "${GATEWAY_SERVICE_URL}"
)
public interface NotificationClient {

    @PostMapping("/api/notifications")
    void createNotification(
        @RequestBody NotificationRequest request
    );
}
