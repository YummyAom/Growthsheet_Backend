package com.growthsheet.payment_service.config.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.growthsheet.payment_service.config.FeignOkHttpConfig;
import com.growthsheet.payment_service.dto.OrderResponse;

@FeignClient(name = "order-service", url = "${GATEWAY_SERVICE_URL}", configuration = FeignOkHttpConfig.class)
public interface OrderClient {

        @GetMapping("/order/pending")
        List<OrderResponse> getPendingOrders(
                        @RequestHeader("X-USER-ID") UUID userId);

        @GetMapping("/order/{orderId}")
        OrderResponse getOrderById(
                        @RequestHeader("X-USER-ID") UUID userId,
                        @PathVariable("orderId") UUID orderId);

        @PatchMapping("/order/{orderId}/paid")
        void markOrderAsPaid(@PathVariable("orderId") UUID orderId);

        @PatchMapping("/order/internal/items/{orderItemId}/revoke")
        void revokeOrderItemAccess(@PathVariable("orderItemId") UUID orderItemId);

    @GetMapping("{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @RequestHeader("X-USER-ID") UUID userId,
            @PathVariable("orderId") UUID orderId);
}