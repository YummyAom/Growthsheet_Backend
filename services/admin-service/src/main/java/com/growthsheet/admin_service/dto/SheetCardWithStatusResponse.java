package com.growthsheet.admin_service.dto;

import java.math.BigDecimal;
import java.util.UUID;
import com.growthsheet.admin_service.dto.SellerDTO;

public record SheetCardWithStatusResponse(
        UUID id,
        String title,
        BigDecimal price,
        SellerDTO seller,
        String thumbnailUrl,
        Boolean isPublished,
        String status          // ✅ เพิ่ม field นี้
) {}