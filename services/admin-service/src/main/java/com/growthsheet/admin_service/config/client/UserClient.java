package com.growthsheet.admin_service.config.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.growthsheet.admin_service.config.FeignOkHttpConfig;
import com.growthsheet.admin_service.dto.UpdateUserRoleRequest;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${GATEWAY_SERVICE_URL}", configuration = FeignOkHttpConfig.class)
public interface UserClient {

    @GetMapping("/users/{id}")
    UserResponse getUserById(@PathVariable("id") UUID id);

    @PutMapping("/users/{userId}/role")
    void updateUserRole(
            @PathVariable("userId") UUID userId,
            @RequestBody UpdateUserRoleRequest request);
}

record UserResponse(UUID id, String username, String email, String fullName) {
}