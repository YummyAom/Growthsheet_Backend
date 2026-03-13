package com.growthsheet.admin_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.growthsheet.admin_service.config.client.NotificationClient;
import com.growthsheet.admin_service.config.client.UserClient;
import com.growthsheet.admin_service.dto.NotificationRequest;
import com.growthsheet.admin_service.dto.SellerApplicationDetailDTO;
import org.springframework.transaction.annotation.Transactional;

import com.growthsheet.admin_service.dto.SellerApplicationSummaryDTO;
import com.growthsheet.admin_service.dto.SellerReviewResponse;
import com.growthsheet.admin_service.dto.UpdateUserRoleRequest;
import com.growthsheet.admin_service.entity.SellerDetails;
import com.growthsheet.admin_service.entity.SellerReviewLog;
import com.growthsheet.admin_service.entity.SellerStatus;
import com.growthsheet.admin_service.mapper.SellerApplicationMapper;
import com.growthsheet.admin_service.repository.SellerDetailsRepository;
import com.growthsheet.admin_service.repository.SellerReviewLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerAdminService {

    private final SellerDetailsRepository sellerDetailsRepository;
    private final SellerApplicationMapper mapper;
    private final UserClient userClient;
    private final SellerReviewLogRepository sellerReviewLogRepository;
    private final NotificationClient notificationClient;

    public Page<SellerApplicationSummaryDTO> getSellerApplications(
            String status,
            Pageable pageable) {

        Page<SellerDetails> page = sellerDetailsRepository.findByStatus(status, pageable);

        return page.map(this::mapToSummaryDTO);
    }

    private SellerApplicationSummaryDTO mapToSummaryDTO(SellerDetails entity) {
        SellerApplicationSummaryDTO dto = new SellerApplicationSummaryDTO();
        dto.setUser_id(entity.getUserId());
        dto.setFull_name(entity.getFullName());
        dto.setPen_name(entity.getPenName());
        dto.setUniversity(entity.getUniversity());
        dto.setIs_verified(entity.getStatus());
        dto.setCreated_at(entity.getCreatedAt());
        return dto;
    }

    @Transactional
    public SellerReviewResponse reviewSeller(
            UUID userId,
            SellerStatus status,
            String adminComment,
            UUID adminId) {

        SellerDetails entity = sellerDetailsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seller application not found"));

        if (entity.getStatus() != SellerStatus.PENDING) {
            throw new RuntimeException("Application already reviewed");
        }

        if (status == SellerStatus.REJECTED && (adminComment == null || adminComment.isBlank())) {
            throw new RuntimeException("Admin comment is required for rejection");
        }

        // 1. อัปเดตตาราง seller_details
        entity.setStatus(status);
        entity.setAdminComment(status == SellerStatus.REJECTED ? adminComment : null);
        entity.setReviewedBy(adminId);
        entity.setReviewedAt(LocalDateTime.now());
        sellerDetailsRepository.save(entity);

        // 2. ถ้าอนุมัติให้ไปอัปเดต Role ผู้ใช้งาน
        if (status == SellerStatus.APPROVED) {
            userClient.updateUserRole(userId, new UpdateUserRoleRequest("SELLER"));
        }

        // 3. ✨ บันทึก Log การตรวจลงตาราง seller_review_logs ✨
        SellerReviewLog reviewLog = new SellerReviewLog();
        reviewLog.setUserId(userId);
        reviewLog.setAdminId(adminId);
        reviewLog.setAction(status.name()); // "APPROVED" หรือ "REJECTED"
        reviewLog.setComment(status == SellerStatus.REJECTED ? adminComment : null);
        sellerReviewLogRepository.save(reviewLog);

        try {

            String title;
            String message;

            if (status == SellerStatus.APPROVED) {
                title = "สมัคร Seller สำเร็จ 🎉";
                message = "บัญชีของคุณได้รับการอนุมัติให้เป็นผู้ขายแล้ว สามารถเริ่มขายชีทได้ทันที";
            } else {
                title = "การสมัคร Seller ไม่ผ่าน";
                message = "คำขอสมัคร Seller ของคุณไม่ผ่านการอนุมัติ\nเหตุผล: " + adminComment;
            }

            NotificationRequest request = new NotificationRequest(
                    userId,
                    title,
                    message);

            notificationClient.createNotification(request);

        } catch (Exception e) {
            log.error("Failed to send notification", e);
        }

        String message = status == SellerStatus.APPROVED
                ? "Seller approved successfully"
                : "Seller rejected successfully";

        return new SellerReviewResponse(
                message,
                entity.getFullName(),
                entity.getPenName(),
                entity.getStatus(),
                entity.getAdminComment());
    }

    public SellerApplicationDetailDTO getSellerDetail(UUID userId) {

        SellerDetails entity = sellerDetailsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seller application not found"));

        return mapper.toDetailDTO(entity);
    }

}