package com.growthsheet.admin_service.dto.sheets;

import java.math.BigDecimal;
import java.util.UUID;

public record SheetCardResponse(
        UUID id,
        String title,
        BigDecimal price,
        String thumbnailUrl,
        Boolean isPublished
) {}