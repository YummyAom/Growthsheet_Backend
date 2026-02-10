package com.growthsheet.order_service.config.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

// url ดึงจาก environment variable เหมือนเดิม
@FeignClient(name = "user-service", url = "${AUTH_SERVICE_URL}")
public interface UserClient {

    @GetMapping("/api/auth/{id}")
    UserResponse getUserById(@PathVariable("id") UUID id);
}

// DTO สำหรับรับข้อมูลจาก User Service
record UserResponse(UUID id, String username, String email, String fullName) {}