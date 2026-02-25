package com.growthsheet.product_service.config.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.growthsheet.product_service.config.FeignOkHttpConfig;
import com.growthsheet.product_service.dto.UserProfileResponse;

@FeignClient(name = "user-service", url = "{USER_SERVICE_URL}", configuration = FeignOkHttpConfig.class)
public interface UserClient {
    @GetMapping("/{id}") 
    UserProfileResponse getUserById(@PathVariable("id") UUID id);
}
