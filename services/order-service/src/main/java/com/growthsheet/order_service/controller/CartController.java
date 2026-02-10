package com.growthsheet.order_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.order_service.config.client.ProductClient;
import com.growthsheet.order_service.dto.response.AddToCartRequest;
import com.growthsheet.order_service.dto.response.CartResponse;
import com.growthsheet.order_service.dto.response.ProductResponse;
import com.growthsheet.order_service.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final ProductClient productClient;

    public CartController(CartService cartService, ProductClient productClient) {
        this.cartService = cartService;
        this.productClient = productClient;
    }

    @GetMapping
    public String hello() {
        return "Hello cart";
    }

    // @GetMapping("/debug-product/{sheetId}")
    // public ResponseEntity<ProductResponse> debugProduct(@PathVariable UUID
    // sheetId) {
    // // เรียกไปที่ product-service ตรงๆ ผ่าน Feign Client
    // ProductResponse product = productClient.getSheetById(sheetId);
    // return ResponseEntity.ok(product);
    // }
    
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestBody UUID sheetId) {

        return ResponseEntity.ok(cartService.addToCart(userId, sheetId));
    }

    @GetMapping("/user")
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader("X-USER-ID") UUID userId) {

        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            @RequestHeader("X-USER-ID") UUID userId,
            @PathVariable UUID cartItemId) {

        cartService.removeItem(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

}