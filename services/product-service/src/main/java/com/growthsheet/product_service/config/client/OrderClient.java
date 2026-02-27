package com.growthsheet.product_service.config.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.growthsheet.product_service.config.FeignOkHttpConfig;
import com.growthsheet.product_service.dto.client.OrderResponse;

@FeignClient(name = "order-service", url = "${ORDER_SERVICE_URL}", configuration = FeignOkHttpConfig.class)
public interface OrderClient {
    @GetMapping("/api/order/user")
    List<OrderResponse> getOrdersByUser(@RequestHeader("X-USER-ID") UUID userId);
}