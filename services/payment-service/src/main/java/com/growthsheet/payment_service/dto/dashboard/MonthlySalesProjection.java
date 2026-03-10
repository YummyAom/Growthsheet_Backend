package com.growthsheet.payment_service.dto.dashboard;

import java.math.BigDecimal;

public interface MonthlySalesProjection {
    Integer getYear();
    Integer getMonth();
    BigDecimal getTotalAmount();
}