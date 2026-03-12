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
            @RequestHeader("X-USER-ID") UUID sellerId,
            @RequestParam(defaultValue = "7d") String period) { // 👈 เพิ่มรับพารามิเตอร์ period (ค่าเริ่มต้น 7 วัน)

        SellerAnalyticsResponse response = analyticsService.getSellerAnalytics(sellerId, period); // 👈 ส่ง period
                                                                                                  // เข้าไปใน service

        return ResponseEntity.ok(response);
    }
}