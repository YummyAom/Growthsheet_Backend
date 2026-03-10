package com.growthsheet.admin_service.dto;

public record DownloadResponse(
        String fileUrl,
        String sheetName
) {}