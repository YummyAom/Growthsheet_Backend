package com.growthsheet.admin_service.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.growthsheet.admin_service.config.client.ProductClient;
import com.growthsheet.admin_service.dto.sheets.PageResponse;
import com.growthsheet.admin_service.dto.sheets.SheetReportResponse;

import lombok.RequiredArgsConstructor;

/**
 * Service สำหรับ admin จัดการ Report ชีท
 */
@Service
@RequiredArgsConstructor
public class SheetReportAdminService {

    private final ProductClient productClient;

    @Value("${internal.service.token}")
    private String internalServiceToken;

    /**
     * ดึง report ทั้งหมด (กรองตาม status ได้)
     */
    public PageResponse<SheetReportResponse> getReports(String status, int page, int size) {
        return productClient.getReports(internalServiceToken, status, page, size);
    }

    /**
     * ดึง report ทั้งหมดของ sheet นั้น
     */
    public PageResponse<SheetReportResponse> getReportsBySheetId(UUID sheetId, int page, int size) {
        return productClient.getReportsBySheetId(internalServiceToken, sheetId, page, size);
    }

    /**
     * Admin ตรวจสอบ report แล้ว - อัปเดตสถานะ (REVIEWED หรือ DISMISSED)
     */
    public SheetReportResponse reviewReport(UUID reportId, UUID adminId, String status, String adminNote, Boolean suspendSheet) {
        return productClient.reviewReport(internalServiceToken, reportId, status, adminNote, suspendSheet, adminId);
    }
}
