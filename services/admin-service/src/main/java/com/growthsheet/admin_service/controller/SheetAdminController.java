package com.growthsheet.admin_service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.admin_service.config.client.ProductClient;
import com.growthsheet.admin_service.dto.sheets.AdminSheetDetailResponse;
import com.growthsheet.admin_service.dto.sheets.PageResponse;
import com.growthsheet.admin_service.dto.sheets.SheetCardResponse;
import com.growthsheet.admin_service.dto.sheets.SheetDetailResponse;
import com.growthsheet.admin_service.entity.SheetReviewLog;
import com.growthsheet.admin_service.repository.SheetReviewLogRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/sheets-applications")
@RequiredArgsConstructor
public class SheetAdminController {
    private final ProductClient productClient;
    private final SheetReviewLogRepository logRepository;

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

    public void approve(UUID sheetId, UUID adminId, UUID sellerId) {

        // 1. update product
        productClient.approveSheet(sheetId);

        // 2. save log (no comment)
        SheetReviewLog log = new SheetReviewLog();
        log.setSheetId(sheetId);
        log.setSellerId(sellerId);
        log.setAdminId(adminId);
        log.setAction("APPROVED");
        log.setComment(null);

        logRepository.save(log);
    }

    public void reject(UUID sheetId, UUID adminId, UUID sellerId, String comment) {

        productClient.rejectSheet(sheetId);

        SheetReviewLog log = new SheetReviewLog();
        log.setSheetId(sheetId);
        log.setSellerId(sellerId);
        log.setAdminId(adminId);
        log.setAction("REJECTED");
        log.setComment(comment);

        logRepository.save(log);
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

        return new AdminSheetDetailResponse(
                sheet.id(),
                sheet.title(),
                sheet.description(),
                sheet.status(),
                sheet.isPublished(),
                sheet.sellerId(),
                sheet.imageUrl(), 
                lastAction,
                lastComment);
    }

}
