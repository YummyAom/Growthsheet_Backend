package com.growthsheet.product_service.dto;

public record UploadedFile(
        String url,
        Integer pageCount
) {}