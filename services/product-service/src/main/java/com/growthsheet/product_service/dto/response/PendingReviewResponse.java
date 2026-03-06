package com.growthsheet.product_service.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class PendingReviewResponse {

    private UUID sheetId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private BigDecimal averageRating;
    private String category;
    private String courseName;

}