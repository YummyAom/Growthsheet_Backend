package com.growthsheet.admin_service.controller;

import com.growthsheet.admin_service.dto.dashboard.DashboardDTOs;
import com.growthsheet.admin_service.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardDTOs.DashboardSummaryResponse> getSummary(
            @RequestHeader("X-USER-ID") UUID adminId
    ) {
        return ResponseEntity.ok(dashboardService.getSummary(adminId));
    }

    @GetMapping("/revenue")
    public ResponseEntity<DashboardDTOs.RevenueChartResponse> getRevenueChart(
            @RequestHeader("X-USER-ID") UUID adminId,
            @RequestParam(defaultValue = "30d") String range
    ) {
        return ResponseEntity.ok(dashboardService.getRevenueChart(adminId, range));
    }

    @GetMapping("/user-growth")
    public ResponseEntity<DashboardDTOs.UserGrowthResponse> getUserGrowth(
            @RequestHeader("X-USER-ID") UUID adminId,
            @RequestParam(defaultValue = "30d") String range
    ) {
        return ResponseEntity.ok(dashboardService.getUserGrowth(adminId, range));
    }

    @GetMapping("/top-sellers")
    public ResponseEntity<List<DashboardDTOs.TopSellerResponse>> getTopSellers(
            @RequestHeader("X-USER-ID") UUID adminId,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(dashboardService.getTopSellers(adminId, limit));
    }

    @GetMapping("/top-sheets")
    public ResponseEntity<List<DashboardDTOs.TopSheetResponse>> getTopSheets(
            @RequestHeader("X-USER-ID") UUID adminId,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(dashboardService.getTopSheets(adminId, limit));
    }

    @GetMapping("/pending-actions")
    public ResponseEntity<DashboardDTOs.PendingActionsResponse> getPendingActions(
            @RequestHeader("X-USER-ID") UUID adminId
    ) {
        return ResponseEntity.ok(dashboardService.getPendingActions(adminId));
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<DashboardDTOs.ActivityResponse>> getRecentActivity(
            @RequestHeader("X-USER-ID") UUID adminId
    ) {
        return ResponseEntity.ok(dashboardService.getRecentActivity(adminId));
    }

    @GetMapping("/system-health")
    public ResponseEntity<DashboardDTOs.SystemHealthResponse> getSystemHealth(
            @RequestHeader("X-USER-ID") UUID adminId
    ) {
        return ResponseEntity.ok(dashboardService.getSystemHealth(adminId));
    }
}