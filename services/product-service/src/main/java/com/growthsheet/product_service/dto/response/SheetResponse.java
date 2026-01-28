package com.growthsheet.product_service.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record SheetResponse(
    UUID id,
    String title,
    BigDecimal price,
    String status
) {}
