package com.growthsheet.product_service.dto.response;

import java.util.UUID;

public record SheetPerformanceDTO(

        UUID sheetId,

        String title,

        long salesVolume

) {}