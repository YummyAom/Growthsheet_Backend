package com.growthsheet.product_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record SellerReviewResponse(
    // ข้อมูล Sheet — ให้ frontend navigate ไปหน้า /sheets/{sheetId}
    UUID sheetId,
    String sheetTitle,
    String thumbnailUrl,

    // ข้อมูล Review
    UUID reviewId,
    int rating,
    String comment,
    LocalDateTime createdAt,

    // ข้อมูล Reviewer
    UUID reviewerId,
    String reviewerName,
    String reviewerAvatarUrl
) {}