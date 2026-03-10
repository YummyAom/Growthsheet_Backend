package com.growthsheet.admin_service.config.client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.growthsheet.admin_service.config.FeignOkHttpConfig;
import com.growthsheet.admin_service.dto.DownloadResponse;
import com.growthsheet.admin_service.dto.RejectRequest;
import com.growthsheet.admin_service.dto.sheets.PageResponse;
import com.growthsheet.admin_service.dto.sheets.SheetCardResponse;
import com.growthsheet.admin_service.dto.sheets.SheetDetailResponse;
import com.growthsheet.admin_service.dto.sheets.SheetReportResponse;

@FeignClient(name = "product-service", url = "${GATEWAY_SERVICE_URL}", configuration = FeignOkHttpConfig.class)
public interface ProductClient {
        @GetMapping("/products/{sheetId}")
        SheetDetailResponse getSheetById(@PathVariable("sheetId") UUID sheetId);

        @GetMapping("/products")
        PageResponse<SheetCardResponse> getSheets(
                        @RequestParam int page,
                        @RequestParam int size,
                        @RequestParam String sort,
                        @RequestParam(required = false) Boolean isPublished);

        @PatchMapping("/products/{sheetId}/approve")
        void approveSheet(
                        @PathVariable UUID sheetId,
                        @RequestHeader("X-INTERNAL-TOKEN") String token);

        @PatchMapping("/products/{sheetId}/reject")
        void rejectSheet(
                        @PathVariable UUID sheetId,
                        @RequestHeader("X-INTERNAL-TOKEN") String token,
                        @RequestBody RejectRequest request);

        // ===== Sheet Report Endpoints =====

        /**
         * ดึง report ทั้งหมด (กรองตาม status ได้)
         */
        @GetMapping("/products/reports")
        PageResponse<SheetReportResponse> getReports(
                        @RequestHeader("X-INTERNAL-TOKEN") String token,
                        @RequestParam(required = false) String status,
                        @RequestParam int page,
                        @RequestParam int size);

        /**
         * ดึง report ทั้งหมดของ sheet นั้น
         */
        @GetMapping("/products/{sheetId}/reports")
        PageResponse<SheetReportResponse> getReportsBySheetId(
                        @RequestHeader("X-INTERNAL-TOKEN") String token,
                        @PathVariable UUID sheetId,
                        @RequestParam int page,
                        @RequestParam int size);

        /**
         * Admin review report (อัปเดต status)
         */
        @PatchMapping("/products/reports/{reportId}/review")
        SheetReportResponse reviewReport(
                        @RequestHeader("X-INTERNAL-TOKEN") String token,
                        @PathVariable UUID reportId,
                        @RequestParam String status,
                        @RequestParam(required = false) String adminNote,
                        @RequestParam(required = false) Boolean suspendSheet,
                        @RequestParam UUID adminId);

        @GetMapping("/products/{sheetId}/adminDownload")
        ResponseEntity<DownloadResponse> adminDownload(@PathVariable UUID sheetId);

}

record ProductResponse(
                UUID id,
                String title,
                SellerInfo seller,
                BigDecimal price) {
        public record SellerInfo(
                        UUID id,
                        String name) {
        }
}
