package com.growthsheet.payment_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
    String omiseSourceId,
    BigDecimal amount,
    UUID orderId
) {}