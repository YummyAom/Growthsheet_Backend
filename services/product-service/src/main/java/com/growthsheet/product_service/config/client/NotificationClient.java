package com.growthsheet.product_service.config.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.growthsheet.product_service.dto.request.NotificationRequest;

@FeignClient(
    name = "notification-service",
    url = "${GATEWAY_SERVICE_URL}"
)
public interface NotificationClient {

    @PostMapping("/notifications/internal")
    void createNotification(
        @RequestBody NotificationRequest request
    );
}
