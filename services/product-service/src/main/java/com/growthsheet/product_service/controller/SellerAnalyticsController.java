package com.growthsheet.product_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.growthsheet.product_service.dto.response.SellerAnalyticsResponse;
import com.growthsheet.product_service.service.SellerAnalyticsService;

@RestController
@RequestMapping("/api/products/analytics")
public class SellerAnalyticsController {

    private final SellerAnalyticsService analyticsService;

    public SellerAnalyticsController(SellerAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<SellerAnalyticsResponse> getSummary(
            @RequestHeader("X-USER-ID") UUID sellerId) {

        SellerAnalyticsResponse response =
                analyticsService.getSellerAnalytics(sellerId);

        return ResponseEntity.ok(response);
    }
}