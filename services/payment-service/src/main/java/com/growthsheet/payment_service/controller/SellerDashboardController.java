package com.growthsheet.payment_service.controller;

import com.growthsheet.payment_service.dto.dashboard.SellerDashboardDTOs.SellerDashboardSummaryResponse;
import com.growthsheet.payment_service.service.SellerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments/seller/dashboard")
@RequiredArgsConstructor
public class SellerDashboardController {

    private final SellerDashboardService sellerDashboardService;

    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary(@RequestHeader("X-USER-ID") UUID sellerId) {
        try {
            SellerDashboardSummaryResponse summary = sellerDashboardService.getSellerDashboardSummary(sellerId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", summary
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
