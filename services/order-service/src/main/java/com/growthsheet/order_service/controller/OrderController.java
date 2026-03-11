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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.order_service.dto.PageResponse;
import com.growthsheet.order_service.dto.request.CheckoutRequest;
import com.growthsheet.order_service.dto.response.OrderResponse;
import com.growthsheet.order_service.entity.Order;
import com.growthsheet.order_service.service.OrderService;

import org.springframework.data.domain.Pageable;

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

    // เปลี่ยนจาก ResponseEntity<Order> เป็น ResponseEntity<OrderResponse>
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestBody CheckoutRequest checkoutRequest) {

        // ค่าที่ได้จาก orderService.checkout จะเป็น OrderResponse แล้ว
        return ResponseEntity.ok(orderService.checkout(userId, checkoutRequest));
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestHeader("X-USER-ID") UUID userId) {

        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/user/paid")
    public ResponseEntity<PageResponse<OrderResponse>> getPaidOrders(
            @RequestHeader("X-USER-ID") UUID userId,
            Pageable pageable) {

        return ResponseEntity.ok(
                orderService.getPaidOrdersByUser(userId, pageable));
    }

    @GetMapping("/user/paid/check")
    public ResponseEntity<Boolean> hasPurchased(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestParam UUID sheetId) {

        return ResponseEntity.ok(
                orderService.hasPurchased(userId, sheetId));
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

    // ===== ยกเลิก Order (ยกเลิกได้เฉพาะ PENDING) =====
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<java.util.Map<String, String>> cancelOrder(
            @RequestHeader("X-USER-ID") UUID userId,
            @PathVariable UUID orderId) {

        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok(java.util.Map.of(
                "status", "success",
                "message", "ยกเลิก Order เรียบร้อยแล้ว"));
    }

    @PatchMapping("/internal/items/{orderItemId}/revoke")
    public ResponseEntity<Void> revokeAccess(
            @PathVariable UUID orderItemId) {

        orderService.revokeAccess(orderItemId);
        return ResponseEntity.ok().build();
    }
}
