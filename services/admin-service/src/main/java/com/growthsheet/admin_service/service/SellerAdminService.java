
package com.growthsheet.admin_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.growthsheet.admin_service.dto.SellerApplicationDetailDTO;
import org.springframework.transaction.annotation.Transactional;

import com.growthsheet.admin_service.dto.SellerApplicationSummaryDTO;
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
        dto.setIs_verified(entity.getIsVerified());
        dto.setCreated_at(entity.getCreatedAt());
        return dto;
    }

    @Transactional
    public SellerApplicationDetailDTO reviewSeller(
            UUID userId,
            String status,
            String adminComment,
            UUID adminId) {

        // 1. ดึง Entity จาก DB
        SellerDetails entity = sellerDetailsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seller application not found"));

        // 2. แปลง status → Enum
        SellerStatus newStatus = SellerStatus.valueOf(status.toUpperCase());

        // 3. อัปเดตข้อมูล
        entity.setIsVerified(newStatus);
        entity.setAdminComment(adminComment);
        entity.setReviewedBy(adminId);
        entity.setReviewedAt(LocalDateTime.now());

        // 4. save
        sellerDetailsRepository.save(entity);

        // 5. map เป็น DTO ส่งกลับ
        return mapper.toDetailDTO(entity);
    }

    public SellerApplicationDetailDTO getSellerDetail(UUID userId) {

    SellerDetails entity = sellerDetailsRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Seller application not found"));

    return mapper.toDetailDTO(entity);
}

}