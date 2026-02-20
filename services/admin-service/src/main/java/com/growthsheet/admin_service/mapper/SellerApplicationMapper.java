package com.growthsheet.admin_service.mapper;

import org.springframework.stereotype.Component;

import com.growthsheet.admin_service.dto.SellerApplicationDetailDTO;
import com.growthsheet.admin_service.entity.SellerDetails;

@Component
public class SellerApplicationMapper {

    public SellerApplicationDetailDTO toDetailDTO(SellerDetails entity) {
        SellerApplicationDetailDTO dto = new SellerApplicationDetailDTO();

        dto.setUser_id(entity.getUser_id());
        dto.setPen_name(entity.getPen_name());
        dto.setFull_name(entity.getFull_name());
        dto.setUniversity(entity.getUniversity());
        dto.setStudent_id(entity.getStudent_id());

        dto.setId_card_url(entity.getId_card_url());
        dto.setSelfie_id_url(entity.getId_card_url());

        dto.setPhone_number(entity.getPhone_number());

        dto.setBank_name(entity.getBank_name());
        dto.setBank_account_number(entity.getBank_account_number());
        dto.setBank_account_name(entity.getBank_account_name());

        dto.setIs_verified(entity.getIs_verified().name());
        dto.setAdmin_comment(entity.getAdmin_comment());

        dto.setReviewed_by(entity.getReviewed_by());
        dto.setReviewed_at(entity.getReviewed_at());

        dto.setCreated_at(entity.getCreated_at());
        dto.setUpdated_at(entity.getUpdated_at());

        return dto;
    }
}