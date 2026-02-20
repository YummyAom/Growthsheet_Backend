package com.growthsheet.admin_service.config.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "user-service", url = "${GATEWAY_SERVICE_URL}")
public interface UserClient {

    @GetMapping("/users/{id}")
    UserResponse getUserById(@PathVariable("id") UUID id);
}

record UserResponse(UUID id, String username, String email, String fullName) {}