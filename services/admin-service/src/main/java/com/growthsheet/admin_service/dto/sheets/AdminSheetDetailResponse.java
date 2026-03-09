package com.growthsheet.admin_service.dto.sheets;

import java.util.UUID;

import com.growthsheet.admin_service.dto.SellerDTO;

public record AdminSheetDetailResponse(
        UUID id,
        String title,
        String description,
        String status,
        Boolean isPublished,
        SellerDTO seller,   // ← ใช้ object แทน
        String imageUrl,
        String fileUrl,
        String lastAction,
        String lastComment
) {}