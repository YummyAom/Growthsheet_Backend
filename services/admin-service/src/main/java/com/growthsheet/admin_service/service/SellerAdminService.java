package com.growthsheet.admin_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.growthsheet.admin_service.dto.SellerApplicationSummaryDTO;
import com.growthsheet.admin_service.entity.SellerDetails;
import com.growthsheet.admin_service.repository.SellerDetailsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerAdminService {

    private final SellerDetailsRepository sellerDetailsRepository;

    public Page<SellerApplicationSummaryDTO> getSellerApplications(
            String status,
            Pageable pageable
    ) {

        Page<SellerDetails> page =
                sellerDetailsRepository.findByStatus(status, pageable);

        return page.map(this::mapToSummaryDTO);
    }

    private SellerApplicationSummaryDTO mapToSummaryDTO(SellerDetails entity) {
        SellerApplicationSummaryDTO dto = new SellerApplicationSummaryDTO();
        dto.setUser_id(entity.getUser_id());
        dto.setFull_name(entity.getFull_name());
        dto.setPen_name(entity.getPen_name());
        dto.setUniversity(entity.getUniversity());
        dto.setIs_verified(entity.getIs_verified());
        dto.setCreated_at(entity.getCreated_at());
        return dto;
    }
}