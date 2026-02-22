package com.growthsheet.admin_service.dto.sheets;

import java.util.UUID;

public record AdminSheetDetailResponse(
        UUID id,
        String title,
        String description,
        String status,
        Boolean isPublished,
        UUID sellerId,
        String imageUrl,
        String lastAction,
        String lastComment
) {}