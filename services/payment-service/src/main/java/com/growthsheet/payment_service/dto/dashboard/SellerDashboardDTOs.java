package com.growthsheet.payment_service.dto.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class SellerDashboardDTOs {

    @Getter @Setter @Builder
    public static class SellerDashboardSummaryResponse {
        private Long totalSalesVolume;
        private BigDecimal totalRevenue;
        private BigDecimal withdrawableAmount;
        private List<SheetPerformanceProjection> sheetPerformances;
    }
}
