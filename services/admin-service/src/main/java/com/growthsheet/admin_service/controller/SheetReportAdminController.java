package com.growthsheet.admin_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.admin_service.dto.sheets.PageResponse;
import com.growthsheet.admin_service.dto.sheets.ReviewReportRequest;
import com.growthsheet.admin_service.dto.sheets.SheetReportResponse;
import com.growthsheet.admin_service.service.SheetReportAdminService;

import lombok.RequiredArgsConstructor;

/**
 * Controller สำหรับ Admin จัดการ Report ชีท
 */
@RestController
@RequestMapping("/api/admin/sheet-reports")
@RequiredArgsConstructor
public class SheetReportAdminController {

    private final SheetReportAdminService reportService;

    /**
     * ดึง report ทั้งหมด (กรองตาม status ได้)
     * GET /api/admin/sheet-reports?status=PENDING&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<PageResponse<SheetReportResponse>> getReports(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(reportService.getReports(status, page, size));
    }

    /**
     * ดึง report ทั้งหมดของ sheet นั้น
     * GET /api/admin/sheet-reports/sheets/{sheetId}?page=0&size=10
     */
    @GetMapping("/sheets/{sheetId}")
    public ResponseEntity<PageResponse<SheetReportResponse>> getReportsBySheet(
            @PathVariable UUID sheetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(reportService.getReportsBySheetId(sheetId, page, size));
    }

    /**
     * Admin review report (เปลี่ยนสถานะเป็น REVIEWED หรือ DISMISSED)
     * PATCH /api/admin/sheet-reports/{reportId}/review
     * Body: { "status": "REVIEWED", "adminNote": "ตรวจสอบแล้ว..." }
     */
    @PatchMapping("/{reportId}/review")
    public ResponseEntity<SheetReportResponse> reviewReport(
            @PathVariable UUID reportId,
            @RequestHeader("X-USER-ID") UUID adminId,
            @RequestBody ReviewReportRequest request) {

        SheetReportResponse response = reportService.reviewReport(
                reportId,
                adminId,
                request.getStatus(),
                request.getAdminNote()
        );

        return ResponseEntity.ok(response);
    }
}
