package com.growthsheet.product_service.config.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.growthsheet.product_service.config.FeignOkHttpConfig;
import com.growthsheet.product_service.dto.PageResponse;
import com.growthsheet.product_service.dto.client.OrderResponse;

import org.springframework.data.domain.Pageable;

@FeignClient(name = "order-service", url = "${ORDER_SERVICE_URL}", configuration = FeignOkHttpConfig.class)
public interface OrderClient {

    @GetMapping("/api/order/user/paid/check")
    Boolean hasPurchased(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestParam("sheetId") UUID sheetId);

    @GetMapping("/api/order/user/paid")
    PageResponse<OrderResponse> getPaidOrders(
            @RequestHeader("X-USER-ID") UUID userId,
            @SpringQueryMap Pageable pageable);
}