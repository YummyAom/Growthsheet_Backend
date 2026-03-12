package com.growthsheet.product_service.dto.response;

public record DailySaleDTO(
    String date,
    long amount
) {}