package com.growthsheet.payment_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromptPayResponse(
        String chargeId,
        String qrCodeUrl,
        LocalDateTime expiresAt,
        BigDecimal amount
) {}