package com.growthsheet.product_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record SheetReportResponse(
    UUID id,
    UUID sheetId,
    UUID reporterId,
    String reason,
    String status,
    UUID adminId,
    String adminNote,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String fileUrl
) {}
