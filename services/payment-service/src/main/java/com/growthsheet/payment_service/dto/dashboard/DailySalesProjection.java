package com.growthsheet.payment_service.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailySalesProjection {
    LocalDate getSaleDate();
    BigDecimal getTotalAmount();
}