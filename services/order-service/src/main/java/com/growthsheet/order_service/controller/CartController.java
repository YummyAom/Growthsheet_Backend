package com.growthsheet.order_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.order_service.dto.response.AddToCartRequest;
import com.growthsheet.order_service.dto.response.CartResponse;
import com.growthsheet.order_service.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String hello(){
        return "Hello cart";
    }
    
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestBody AddToCartRequest req) {

        return ResponseEntity.ok(cartService.addToCart(userId, req));
    }

    @GetMapping("/user")
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader("X-USER-ID") UUID userId) {

        return ResponseEntity.ok(cartService.getCart(userId));
    }
}