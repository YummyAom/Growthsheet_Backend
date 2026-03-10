package com.growthsheet.admin_service.config.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "analysis-service", url = "http://165.232.171.127")
public interface AnalysisClient {

    @PostMapping("/sheets/analyze")
    Object analyzeSheet(
        @RequestParam("file_url") String fileUrl,
        @RequestParam("sheet_id") String sheetId
    );
}