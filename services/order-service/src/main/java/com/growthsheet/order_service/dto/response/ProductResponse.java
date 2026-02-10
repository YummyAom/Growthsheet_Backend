package com.growthsheet.order_service.dto.response;

import java.util.UUID;
import java.math.BigDecimal;

public record ProductResponse(
    UUID id,
    String title,
    SellerInfo seller, 
    BigDecimal price
) {
    public record SellerInfo(
        UUID id,
        String name
    ) {}
}