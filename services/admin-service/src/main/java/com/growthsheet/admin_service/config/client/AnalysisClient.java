package com.growthsheet.admin_service.config.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

@FeignClient(name = "analysis-service", url = "http://165.232.171.127")
public interface AnalysisClient {

    @PostMapping(
        value = "/sheets/analyze",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    void analyzeSheet(
        @RequestPart(value = "file_url", required = false) String fileUrl,
        @RequestPart(value = "sheet_id") String sheetId,
        @RequestPart(value = "webhook_url", required = false) String webhookUrl
    );

}