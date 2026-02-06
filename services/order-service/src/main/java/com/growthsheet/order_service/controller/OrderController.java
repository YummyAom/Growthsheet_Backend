package com.growthsheet.order_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    // @GetMapping("/test")
    // public ResponseEntity<List<OrderResponse>> getOrders(
    //         @RequestHeader(value = "X-User-Id", required = false) String userId,
    //         @RequestHeader(value = "X-User-Role", required = false) String role) {

    //     // Debug ดูว่า Gateway ส่งมาให้จริงไหม
    //     System.out.println("Order Service Received User ID: " + userId);
    //     System.out.println("Order Service Received Role: " + role);

    //     if (userId == null) {
    //         return ResponseEntity.status(401).build();
    //     }

    //     return ResponseEntity.ok(orderService.getOrdersByUser(UUID.fromString(userId)));
    // }

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
}
