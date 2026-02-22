package com.growthsheet.admin_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.growthsheet.admin_service.config.client.ProductClient;
import com.growthsheet.admin_service.dto.RejectRequest;
import com.growthsheet.admin_service.entity.SheetReviewLog;
import com.growthsheet.admin_service.repository.SheetReviewLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SheetAdminService {

    private final ProductClient productClient;
    private final SheetReviewLogRepository logRepository;

    @Transactional
    public void approve(UUID sheetId, UUID adminId, UUID sellerId) {

        // 1. update product-service
        productClient.approveSheet(sheetId);

        // 2. save log
        SheetReviewLog log = new SheetReviewLog();
        log.setSheetId(sheetId);
        log.setSellerId(sellerId);
        log.setAdminId(adminId);
        log.setAction("APPROVED");
        log.setComment(null);

        logRepository.save(log);
    }

    @Transactional
    public void reject(UUID sheetId, UUID adminId, UUID sellerId, RejectRequest comment) {

        productClient.rejectSheet(sheetId);

        SheetReviewLog log = new SheetReviewLog();
        log.setSheetId(sheetId);
        log.setSellerId(sellerId);
        log.setAdminId(adminId);
        log.setAction("REJECTED");
        log.setComment(comment.getComment());

        logRepository.save(log);
    }
}