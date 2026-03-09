package com.growthsheet.payment_service.config.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "user-service", url = "${GATEWAY_SERVICE_URL}")
public interface UserClient {

    @GetMapping("/auth/{id}")
    UserResponse getUserById(@PathVariable("id") UUID id);

    @GetMapping("/users/{userId}/bank-info")
    Map<String, String> getSellerBankInfo(@PathVariable("userId") UUID userId);
}

record UserResponse(UUID id, String username, String email, String fullName) {}