package com.growthsheet.admin_service.dto.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DashboardDTOs {

    @Getter @Setter @Builder
    public static class DashboardSummaryResponse {
        private Long totalUsers;
        private Long totalSellers;
        private Long totalSheets;
        private BigDecimal totalRevenue;
        private Long pendingReports;
        private Long pendingWithdrawals;
    }

    @Getter @Setter @Builder
    public static class RevenueChartResponse {
        private String range;
        private List<RevenueData> data;
    }

    @Getter @Setter @Builder
    public static class RevenueData {
        private String date; // YYYY-MM-DD
        private BigDecimal revenue;
    }

    @Getter @Setter @Builder
    public static class UserGrowthResponse {
        private List<UserGrowthData> data;
    }

    @Getter @Setter @Builder
    public static class UserGrowthData {
        private String date;
        private Long users;
    }

    @Getter @Setter @Builder
    public static class TopSellerResponse {
        private UUID sellerId;
        private String sellerName;
        private Long sales;
        private BigDecimal revenue;
    }

    @Getter @Setter @Builder
    public static class TopSheetResponse {
        private UUID sheetId;
        private String title;
        private Long sales;
    }

    @Getter @Setter @Builder
    public static class PendingActionsResponse {
        private Long reports;
        private Long withdrawRequests;
        private Long sellerApplications;
    }

    @Getter @Setter @Builder
    public static class ActivityResponse {
        private String type;
        private String message;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @Builder
    public static class SystemHealthResponse {
        private String database;
        private String storageUsage;
        private Long activeUsers;
    }
}
