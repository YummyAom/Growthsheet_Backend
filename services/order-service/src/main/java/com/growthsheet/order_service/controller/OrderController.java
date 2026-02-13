package com.growthsheet.order_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.order_service.dto.request.CheckoutRequest;
import com.growthsheet.order_service.dto.response.OrderResponse;
import com.growthsheet.order_service.entity.Order;
import com.growthsheet.order_service.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello order";
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestBody CheckoutRequest checkoutRequest) {
        return ResponseEntity.ok(orderService.checkout(userId, checkoutRequest));
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestHeader("X-USER-ID") UUID userId) {

        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<OrderResponse>> getPendingOrders(
            @RequestHeader("X-USER-ID") UUID userId) {

        return ResponseEntity.ok(
                orderService.getPendingOrdersByUser(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @RequestHeader("X-USER-ID") UUID userId,
            @PathVariable("orderId") UUID orderId) {

        return ResponseEntity.ok(
                orderService.getOrderByIdAndUser(orderId, userId));
    }

    @PatchMapping("/{orderId}/paid")
    public ResponseEntity<Void> markOrderAsPaid(
            @PathVariable UUID orderId) {

        orderService.markAsPaid(orderId);
        return ResponseEntity.ok().build();
    }
}
