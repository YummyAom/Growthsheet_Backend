package com.growthsheet.payment_service.config.client;

public record DownloadResponse(
    String fileUrl, 
    String sheetName
) {}