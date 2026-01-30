package com.growthsheet.product_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record SheetReviewResponseDTO(
        UUID id,
        UUID sheetId,
        UUID userId,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}