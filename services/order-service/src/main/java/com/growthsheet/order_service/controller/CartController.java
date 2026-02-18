package com.growthsheet.order_service.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.growthsheet.order_service.config.client.ProductClient;
import com.growthsheet.order_service.dto.request.AddToCartRequest;
import com.growthsheet.order_service.dto.request.RemoveCartItemsRequest;
import com.growthsheet.order_service.dto.response.CartResponse;
import com.growthsheet.order_service.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService, ProductClient productClient) {
        this.cartService = cartService;
    }

    @GetMapping
    public String hello() {
        return "Hello cart";
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestBody AddToCartRequest sheetId) {

        return ResponseEntity.ok(cartService.addToCart(userId, sheetId));
    }

    @GetMapping("/user")
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader("X-USER-ID") UUID userId) {

        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping
    public ResponseEntity<String> removeItems(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestBody RemoveCartItemsRequest request) {

        if (request.getCartItemIds() == null || request.getCartItemIds().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "cartItemIds must not be null or empty");
        }

        cartService.removeItems(userId, request.getCartItemIds());
        return ResponseEntity.ok("Remove success");
    }

}