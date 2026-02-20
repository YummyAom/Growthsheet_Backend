package com.growthsheet.admin_service.mapper;

import org.springframework.stereotype.Component;

import com.growthsheet.admin_service.dto.SellerApplicationDetailDTO;
import com.growthsheet.admin_service.entity.SellerDetails;

@Component
public class SellerApplicationMapper {

    public SellerApplicationDetailDTO toDetailDTO(SellerDetails entity) {
        SellerApplicationDetailDTO dto = new SellerApplicationDetailDTO();

        dto.setUser_id(entity.getUserId());
        dto.setPen_name(entity.getPenName());
        dto.setFull_name(entity.getFullName());
        dto.setUniversity(entity.getUniversity());
        dto.setStudent_id(entity.getStudentId());

        dto.setId_card_url(entity.getIdCardUrl());
        dto.setSelfie_id_url(entity.getSelfieIdUrl());

        dto.setPhone_number(entity.getPhoneNumber());

        dto.setBank_name(entity.getBankName());
        dto.setBank_account_number(entity.getBankAccountNumber());
        dto.setBank_account_name(entity.getBankAccountName());

        // Enum → String
        if (entity.getIsVerified() != null) {
            dto.setIs_verified(entity.getIsVerified().name());
        }

        dto.setAdmin_comment(entity.getAdminComment());

        dto.setReviewed_by(entity.getReviewedBy());
        dto.setReviewed_at(entity.getReviewedAt());

        dto.setCreated_at(entity.getCreatedAt());
        dto.setUpdated_at(entity.getUpdatedAt());

        return dto;
    }
}