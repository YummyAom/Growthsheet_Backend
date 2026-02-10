package com.growthsheet.product_service.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.growthsheet.product_service.dto.CategoryDTO;
import com.growthsheet.product_service.dto.SellerDTO;
import com.growthsheet.product_service.dto.UniversityDTO;

public record SheetCardResponse(
        UUID id,
        String title,
        String description,
        BigDecimal price,
        String image,
        UniversityDTO university,
        CategoryDTO category,
        List<String> hashtags,
        Double averageRating,
        Boolean isPublished,
        SellerDTO seller
) {}