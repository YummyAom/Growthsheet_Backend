package com.growthsheet.payment_service.dto.dashboard;

import java.math.BigDecimal;
import java.util.UUID;

public interface SheetPerformanceProjection {
    UUID getSheetId();
    String getSheetName();
    Long getSalesVolume();
    BigDecimal getTotalRevenue();
}
