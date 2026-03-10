package com.growthsheet.product_service.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.growthsheet.product_service.dto.response.SheetReportResponse;
import com.growthsheet.product_service.entity.ReportStatus;
import com.growthsheet.product_service.entity.SheetReport;
import com.growthsheet.product_service.repository.SheetReportRepository;
import com.growthsheet.product_service.repository.SheetRepository;

import jakarta.transaction.Transactional;

@Service
public class SheetReportService {

    private final SheetReportRepository reportRepo;
    private final SheetRepository sheetRepo;

    public SheetReportService(SheetReportRepository reportRepo, SheetRepository sheetRepo) {
        this.reportRepo = reportRepo;
        this.sheetRepo = sheetRepo;
    }

    /**
     * User กด report sheet พร้อมเหตุผล
     */
    @Transactional
    public SheetReportResponse reportSheet(UUID sheetId, UUID reporterId, String reason) {

        // ตรวจสอบว่ามีชีทนี้อยู่จริงหรือไม่
        if (!sheetRepo.existsById(sheetId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบชีทนี้");
        }

        // ตรวจสอบว่า user เคย report ชีทนี้ไปแล้วหรือยัง
        if (reportRepo.existsBySheetIdAndReporterId(sheetId, reporterId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "คุณได้รายงานชีทนี้ไปแล้ว");
        }

        SheetReport report = new SheetReport();
        report.setSheetId(sheetId);
        report.setReporterId(reporterId);
        report.setReason(reason);
        report.setStatus(ReportStatus.PENDING);

        reportRepo.save(report);

        return toResponse(report);
    }

    /**
     * ดึงรายการ report ทั้งหมดตาม status (สำหรับ internal/admin call)
     */
    public Page<SheetReportResponse> getReports(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<SheetReport> reports;

        if (status != null && !status.isBlank()) {
            ReportStatus reportStatus = ReportStatus.valueOf(status.toUpperCase());
            reports = reportRepo.findByStatus(reportStatus, pageable);
        } else {
            reports = reportRepo.findAll(pageable);
        }

        return reports.map(this::toResponse);
    }

    /**
     * ดึง report ทั้งหมดของชีทนั้น
     */
    public Page<SheetReportResponse> getReportsBySheetId(UUID sheetId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reportRepo.findBySheetId(sheetId, pageable).map(this::toResponse);
    }

    /**
     * Admin ตรวจสอบ report แล้ว - อัปเดต status
     */
    @Transactional
    public SheetReportResponse reviewReport(UUID reportId, UUID adminId, ReportStatus newStatus, String adminNote, Boolean suspendSheet) {
        SheetReport report = reportRepo.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบ report นี้"));

        report.setStatus(newStatus);
        report.setAdminId(adminId);
        report.setAdminNote(adminNote);

        reportRepo.save(report);

        // ตัดสินใจระงับชีทหากมีการส่ง suspendSheet = true
        if (Boolean.TRUE.equals(suspendSheet)) {
            com.growthsheet.product_service.entity.Sheet sheet = sheetRepo.findById(report.getSheetId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบชีทนี้"));
            
            sheet.setIsPublished(false);
            sheetRepo.save(sheet);
        }

        return toResponse(report);
    }

    private SheetReportResponse toResponse(SheetReport report) {
        String fileUrl = null;
        com.growthsheet.product_service.entity.Sheet sheet = sheetRepo.findById(report.getSheetId()).orElse(null);
        if (sheet != null) {
            fileUrl = sheet.getFileUrl();
        }

        return new SheetReportResponse(
                report.getId(),
                report.getSheetId(),
                report.getReporterId(),
                report.getReason(),
                report.getStatus().name(),
                report.getAdminId(),
                report.getAdminNote(),
                report.getCreatedAt(),
                report.getUpdatedAt(),
                fileUrl
        );
    }
}
