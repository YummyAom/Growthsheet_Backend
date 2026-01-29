package com.growthsheet.product_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.growthsheet.product_service.dto.CategoryDTO;
import com.growthsheet.product_service.dto.SellerDTO;
import com.growthsheet.product_service.dto.UniversityDTO;

public record ProductResponseDTO(
    UUID id,
    String title,
    String description,
    BigDecimal price,

    String imageUrl,
    String previewUrl,

    UniversityDTO university,
    CategoryDTO category,

    List<String> tags,

    Integer ratingCount,
    Double ratingAverage,

    SellerDTO seller,
    Boolean isPublished,
    Integer pageCount,

    LocalDateTime createdAt,
    LocalDateTime updatedAt
)
{}