package com.growthsheet.product_service.dto.response;

import java.util.List;

public record SellerAnalyticsResponse(

        long totalSales,

        List<SheetPerformanceDTO> topSheets,

        List<FacultyDistributionDTO> facultyDistribution

) {}