package com.growthsheet.order_service.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.order_service.dto.request.CreateOrderRequest;
import com.growthsheet.order_service.dto.response.AddOrderRespose;
import com.growthsheet.order_service.dto.response.OrderResponse;
import com.growthsheet.order_service.repository.OrderRepository;
import com.growthsheet.order_service.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/addOrder")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestBody CreateOrderRequest req) {

        OrderResponse res = orderService.createOrder(userId, req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }
}
