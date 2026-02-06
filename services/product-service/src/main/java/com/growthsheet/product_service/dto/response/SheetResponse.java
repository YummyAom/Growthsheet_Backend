package com.growthsheet.product_service.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.growthsheet.product_service.entity.Sheet;

public record SheetResponse(
        UUID id,
        String title,
        BigDecimal price,
        String status
) {

    public static SheetResponse from(Sheet sheet) {
        return new SheetResponse(
                sheet.getId(),
                sheet.getTitle(),
                sheet.getPrice(),
                sheet.getStatus().name()
        );
    }
}
