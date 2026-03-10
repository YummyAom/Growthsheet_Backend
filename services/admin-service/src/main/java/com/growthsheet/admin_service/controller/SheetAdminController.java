package com.growthsheet.admin_service.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.admin_service.config.client.AnalysisClient;
import com.growthsheet.admin_service.config.client.ProductClient;
import com.growthsheet.admin_service.dto.DownloadResponse;
import com.growthsheet.admin_service.dto.RejectRequest;
import com.growthsheet.admin_service.dto.sheets.AdminSheetDetailResponse;
import com.growthsheet.admin_service.dto.sheets.PageResponse;
import com.growthsheet.admin_service.dto.sheets.SheetCardResponse;
import com.growthsheet.admin_service.dto.sheets.SheetDetailResponse;
import com.growthsheet.admin_service.entity.SheetReviewLog;
import com.growthsheet.admin_service.repository.SheetReviewLogRepository;
import com.growthsheet.admin_service.service.SheetAdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/sheets-applications")
@RequiredArgsConstructor
public class SheetAdminController {
    private final SheetAdminService sheetAdminService;
    private final ProductClient productClient;
    private final SheetReviewLogRepository logRepository;
    private final AnalysisClient analysisClient;

    @GetMapping("")
    public String getHello() {
        return "hello admin sheets";
    }

    @GetMapping("/")
    public PageResponse<SheetCardResponse> getSheets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort) {

        return productClient.getSheets(page, size, sort, false);
    }

    // @PatchMapping("/{sheetId}/approve")
    // public String approve(
    // @PathVariable UUID sheetId,
    // @RequestHeader("X-USER-ID") UUID adminId) {

    // var sheet = productClient.getSheetById(sheetId);

    // UUID sellerId = null;
    // if (sheet.getSeller() != null) {
    // sellerId = sheet.getSeller().getId();
    // }

    // sheetAdminService.approve(sheetId, adminId, sellerId);

    // return "อนุมัติชีทเรียบร้อยแล้ว";
    // }

    @PatchMapping("/{sheetId}/approve")
    public String approve(
            @PathVariable UUID sheetId,
            @RequestHeader("X-USER-ID") UUID adminId) {

        // 1. ดึงข้อมูล sheet
        var sheet = productClient.getSheetById(sheetId);
        UUID sellerId = sheet.getSeller().getId();

        // 2. ดึง URL PDF
        DownloadResponse download = productClient.adminDownload(sheetId);
        String fileUrl = download.fileUrl();

        // 3. approve sheet ใน product-service
        // productClient.approveSheet(sheetId, "INTERNAL_SECRET_TOKEN");

        // 4. บันทึก log admin
        // sheetAdminService.approve(sheetId, adminId, sellerId);

        // 5. ส่งไป AI วิเคราะห์
        analysisClient.analyzeSheet(fileUrl, sheetId.toString());

        return "อนุมัติชีทเรียบร้อยแล้ว";
    }

    @PatchMapping("/{sheetId}/reject")
    public String reject(
            @PathVariable UUID sheetId,
            @RequestHeader("X-USER-ID") UUID adminId,
            @RequestBody RejectRequest request) {

        var sheet = productClient.getSheetById(sheetId);

        UUID sellerId = null;
        if (sheet.getSeller() != null) {
            sellerId = sheet.getSeller().getId();
        }

        sheetAdminService.reject(sheetId, adminId, sellerId, request);

        return "ปฏิเสธชีทเรียบร้อยแล้ว";
    }

    @GetMapping("/{sheetId}")
    public AdminSheetDetailResponse getSheetDetail(@PathVariable UUID sheetId) {

        SheetDetailResponse sheet = productClient.getSheetById(sheetId);

        SheetReviewLog log = logRepository
                .findTopBySheetIdOrderByCreatedAtDesc(sheetId)
                .orElse(null);

        String lastAction = null;
        String lastComment = null;

        if (log != null) {
            lastAction = log.getAction();
            lastComment = log.getComment();
        }

        // คำนวณ status
        String status;
        if ("REJECTED".equals(lastAction)) {
            status = "REJECTED";
        } else if (Boolean.TRUE.equals(sheet.getIsPublished())) {
            status = "APPROVED";
        } else {
            status = "PENDING";
        }

        return new AdminSheetDetailResponse(
                sheet.getId(),
                sheet.getTitle(),
                sheet.getDescription(),
                status,
                sheet.getIsPublished(),
                sheet.getSeller(), // ← ส่งทั้ง object
                sheet.getImageUrl(),
                sheet.getFileUrl(),
                lastAction,
                lastComment);
    }
}
