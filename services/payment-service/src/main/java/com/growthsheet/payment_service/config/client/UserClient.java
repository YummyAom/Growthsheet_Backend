package com.growthsheet.payment_service.config.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "user-service", url = "https://growthsheet-backend.onrender.com/auth")
public interface UserClient {

    @GetMapping("/api/auth/{id}")
    UserResponse getUserById(@PathVariable("id") UUID id);
}

record UserResponse(UUID id, String username, String email, String fullName) {}