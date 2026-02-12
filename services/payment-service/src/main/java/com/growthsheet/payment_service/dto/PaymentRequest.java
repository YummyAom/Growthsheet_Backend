package com.growthsheet.payment_service.dto;

import java.math.BigDecimal;

public record PaymentRequest(
    String source,
    BigDecimal amount // รับราคาที่ส่งมาจาก Frontend
) {}