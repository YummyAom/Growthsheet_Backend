package com.growthsheet.admin_service.dto.sheets;

import java.util.UUID;

import com.growthsheet.admin_service.dto.SellerDTO;

import lombok.Getter;

@Getter
public class SheetDetailResponse {
    private UUID id;
    private String title;
    private String description;
    private Boolean isPublished;
    private String imageUrl;

    private SellerDTO seller; // ← ใช้อันนี้แทน sellerId
}
