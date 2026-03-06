package com.growthsheet.admin_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.growthsheet.admin_service.dto.WithdrawalRequestSummaryDTO;
import com.growthsheet.admin_service.entity.SellerDetails;
import com.growthsheet.admin_service.entity.WithdrawalRequest;
import com.growthsheet.admin_service.entity.WithdrawStatus;
import com.growthsheet.admin_service.repository.WithdrawalRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithdrawAdminService {

    private final WithdrawalRequestRepository withdrawalRequestRepository;

    public Page<WithdrawalRequestSummaryDTO> getWithdrawalRequests(String status, Pageable pageable) {
    WithdrawStatus withdrawStatus = WithdrawStatus.valueOf(status.toUpperCase());
    return withdrawalRequestRepository.findByStatus(withdrawStatus, pageable)
            .map(this::mapToSummaryDTO);
    }

    private WithdrawalRequestSummaryDTO mapToSummaryDTO(WithdrawalRequest wr) {
        SellerDetails sd = wr.getSellerDetails();

        WithdrawalRequestSummaryDTO dto = new WithdrawalRequestSummaryDTO();
        dto.setId(wr.getId());
        dto.setSeller_id(wr.getSellerId());
        dto.setUser_id(wr.getUserId());
        dto.setAmount(wr.getAmount());
        dto.setStatus(wr.getStatus());
        dto.setBank_name(wr.getBankName());
        dto.setBank_account_number(wr.getBankAccountNumber());
        dto.setBank_account_name(wr.getBankAccountName());
        dto.setNote(wr.getNote());
        dto.setCreated_at(wr.getCreatedAt());

        dto.setSellerPenName(sd != null ? sd.getPenName() : null);
        dto.setSellerFullName(sd != null ? sd.getFullName() : null);

        return dto;
    }
}
