
package com.growthsheet.admin_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.growthsheet.admin_service.config.client.UserClient;
import com.growthsheet.admin_service.dto.SellerApplicationDetailDTO;
import org.springframework.transaction.annotation.Transactional;

import com.growthsheet.admin_service.dto.SellerApplicationSummaryDTO;
import com.growthsheet.admin_service.dto.SellerReviewResponse;
import com.growthsheet.admin_service.dto.UpdateUserRoleRequest;
import com.growthsheet.admin_service.entity.SellerDetails;
import com.growthsheet.admin_service.entity.SellerStatus;
import com.growthsheet.admin_service.mapper.SellerApplicationMapper;
import com.growthsheet.admin_service.repository.SellerDetailsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerAdminService {

    private final SellerDetailsRepository sellerDetailsRepository;
    private final SellerApplicationMapper mapper;
    private final UserClient userClient;

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

    // @Transactional
    // public SellerApplicationDetailDTO reviewSeller(
    // UUID userId,
    // String status,
    // String adminComment,
    // UUID adminId) {
    // System.out.println("Review seller called");
    // SellerDetails entity = sellerDetailsRepository.findByUserId(userId)
    // .orElseThrow(() -> new RuntimeException("Seller application not found"));

    // SellerStatus newStatus = SellerStatus.valueOf(status.toUpperCase());

    // entity.setIsVerified(newStatus);
    // entity.setAdminComment(adminComment);
    // entity.setReviewedBy(adminId);
    // entity.setReviewedAt(LocalDateTime.now());

    // sellerDetailsRepository.save(entity);

    // return mapper.toDetailDTO(entity);
    // }

    @Transactional
    public SellerReviewResponse reviewSeller(
            UUID userId,
            SellerStatus status,
            String adminComment,
            UUID adminId) {

        SellerDetails entity = sellerDetailsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seller application not found"));

        // Prevent double review
        if (entity.getStatus() != SellerStatus.PENDING) {
            throw new RuntimeException("Application already reviewed");
        }

        // Validate reject comment
        if (status == SellerStatus.REJECTED &&
                (adminComment == null || adminComment.isBlank())) {
            throw new RuntimeException("Admin comment is required for rejection");
        }

        entity.setStatus(status);
        entity.setAdminComment(status == SellerStatus.REJECTED ? adminComment : null);
        entity.setReviewedBy(adminId);
        entity.setReviewedAt(LocalDateTime.now());

        sellerDetailsRepository.save(entity);

        if (status == SellerStatus.APPROVED) {
            userClient.updateUserRole(
                    userId,
                    new UpdateUserRoleRequest("SELLER"));
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