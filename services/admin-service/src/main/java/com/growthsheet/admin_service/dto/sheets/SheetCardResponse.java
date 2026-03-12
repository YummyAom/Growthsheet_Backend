package com.growthsheet.admin_service.dto.sheets;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.growthsheet.admin_service.dto.SellerDTO;

public record SheetCardResponse(
                UUID id,
                String title,
                BigDecimal price,
                SellerDTO seller,
                @JsonProperty("image")
                String thumbnailUrl,
                Boolean isPublished) {
}