package com.growthsheet.payment_service.dto.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class SellerDashboardDTOs {

    @Getter @Setter @Builder
    public static class SellerDashboardSummaryResponse {
        // --- เดิม (ยังคงไว้) ---
        private Long totalSalesVolume;
        private BigDecimal totalRevenue;
        private BigDecimal withdrawableAmount;
        private List<SheetPerformanceProjection> sheetPerformances;

        // --- เพิ่มใหม่สำหรับ chart ---
        private BigDecimal todaySales;
        private BigDecimal totalBalance;
        private Long totalOrders;

        // BarChart
        private List<WeeklySalesItem> weeklySales;

        // LineChart
        private List<MonthlySalesItem> monthlySales;

        // Top Sheet Banner
        private String topSheetTitle;
        private BigDecimal topSheetRevenue;
    }

    @Getter @Setter @Builder
    public static class WeeklySalesItem {
        private String day;        // "Mon", "Tue", "Wed", ...
        private BigDecimal amount;
    }

    @Getter @Setter @Builder
    public static class MonthlySalesItem {
        private String month;      // "Jan", "Feb", ...
        private BigDecimal amount;
    }
}