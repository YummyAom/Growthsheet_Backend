package com.growthsheet.order_service.dto.response;

public record DailySaleDTO(
    String date,
    long amount
) {}