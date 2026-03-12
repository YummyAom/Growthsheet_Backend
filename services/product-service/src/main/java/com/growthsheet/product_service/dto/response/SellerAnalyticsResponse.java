package com.growthsheet.product_service.dto.response;

import java.util.List;

public record SellerAnalyticsResponse(

        long totalSales,
        List<DailySaleDTO> dailySales,
        List<SheetPerformanceDTO> topSheets,
        List<FacultyDistributionDTO> facultyDistribution

) {}