package com.growthsheet.admin_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.growthsheet.admin_service.config.client.NotificationClient;
import com.growthsheet.admin_service.config.client.ProductClient;
import com.growthsheet.admin_service.dto.NotificationRequest;
import com.growthsheet.admin_service.dto.RejectRequest;
import com.growthsheet.admin_service.entity.SheetReviewLog;
import com.growthsheet.admin_service.repository.SheetReviewLogRepository;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SheetAdminService {

    private final ProductClient productClient;
    private final SheetReviewLogRepository logRepository;
    private final NotificationClient notificationClient;
    @Value("${internal.service.token}")
    private String internalServiceToken;

    public void approve(UUID sheetId, UUID adminId, UUID sellerId) {

        productClient.approveSheet(sheetId, internalServiceToken);

        SheetReviewLog reviewLog = new SheetReviewLog();
        reviewLog.setSheetId(sheetId);
        reviewLog.setSellerId(sellerId);
        reviewLog.setAdminId(adminId);
        reviewLog.setAction("APPROVED");
        reviewLog.setComment(null);

        logRepository.save(reviewLog);

        NotificationRequest notification = new NotificationRequest();
        notification.setUserId(sellerId);
        notification.setTitle("ชีทของคุณได้รับการอนุมัติ");
        notification.setMessage("ชีทที่คุณอัปโหลดได้รับการอนุมัติและเผยแพร่แล้ว");

        try {
            notificationClient.createNotification(notification);
        } catch (Exception e) {
            log.error("Failed to send approval notification for sheetId={}", sheetId, e);
        }
    }

    @Transactional
    public void reject(UUID sheetId, UUID adminId, UUID sellerId, RejectRequest request) {

        productClient.rejectSheet(sheetId, internalServiceToken, request); // ✅ ส่ง token + request

        SheetReviewLog log = new SheetReviewLog();
        log.setSheetId(sheetId);
        log.setSellerId(sellerId);
        log.setAdminId(adminId);
        log.setAction("REJECTED");
        log.setComment(request.getComment());

        logRepository.save(log);
    }
}
