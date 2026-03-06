package com.growthsheet.admin_service.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.growthsheet.admin_service.config.client.FileClient;
import com.growthsheet.admin_service.dto.WithdrawalApproveResponse;
import com.growthsheet.admin_service.dto.WithdrawalRejectResponse;
import com.growthsheet.admin_service.dto.WithdrawalRequestSummaryDTO;
import com.growthsheet.admin_service.entity.PayoutTransaction;
import com.growthsheet.admin_service.entity.SellerDetails;
import com.growthsheet.admin_service.entity.WithdrawStatus;
import com.growthsheet.admin_service.entity.WithdrawalRequest;
import com.growthsheet.admin_service.repository.PayoutTransactionRepository;
import com.growthsheet.admin_service.repository.WithdrawalRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithdrawAdminService {

    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final PayoutTransactionRepository payoutTransactionRepository;
    private final FileClient fileClient;

    public Page<WithdrawalRequestSummaryDTO> getWithdrawalRequests(String status, Pageable pageable) {
        WithdrawStatus withdrawStatus = WithdrawStatus.valueOf(status.toUpperCase());
        return withdrawalRequestRepository.findByStatus(withdrawStatus, pageable)
                .map(this::mapToSummaryDTO);
    }

    @Transactional
    public WithdrawalApproveResponse approveWithdrawal(UUID withdrawalId, MultipartFile slipFile, UUID adminId) {
        // 1. ดึง withdrawal request และตรวจสอบ status
        WithdrawalRequest wr = withdrawalRequestRepository.findById(withdrawalId)
                .orElseThrow(() -> new RuntimeException("Withdrawal request not found"));

        if (wr.getStatus() != WithdrawStatus.PENDING) {
            throw new RuntimeException("Withdrawal request already reviewed");
        }

        // 2. อัปโหลดสลิปไป Cloudinary ผ่าน file-service
        Map<String, Object> uploadResult = fileClient.uploadSlip(slipFile);
        String slipUrl = (String) uploadResult.get("url");

        // 3. สร้าง PayoutTransaction record
        PayoutTransaction payout = new PayoutTransaction();
        payout.setWithdrawalId(withdrawalId);
        payout.setAdminId(adminId);
        payout.setSlipUrl(slipUrl);
        payout.setTransferredAt(OffsetDateTime.now(ZoneId.of("Asia/Bangkok")));
        payoutTransactionRepository.save(payout);

        // 4. อัปเดต WithdrawalRequest status เป็น APPROVED
        wr.setStatus(WithdrawStatus.APPROVED);
        wr.setReviewedBy(adminId);
        wr.setReviewedAt(LocalDateTime.now(ZoneId.of("Asia/Bangkok")));
        withdrawalRequestRepository.save(wr);

        return new WithdrawalApproveResponse(
                "Withdrawal approved successfully",
                withdrawalId,
                slipUrl,
                WithdrawStatus.APPROVED);
    }

    @Transactional
    public WithdrawalRejectResponse rejectWithdrawal(UUID withdrawalId, String adminComment, UUID adminId) {
        // 1. ดึง withdrawal request และตรวจสอบ status
        WithdrawalRequest wr = withdrawalRequestRepository.findById(withdrawalId)
                .orElseThrow(() -> new RuntimeException("Withdrawal request not found"));

        if (wr.getStatus() != WithdrawStatus.PENDING) {
            throw new RuntimeException("Withdrawal request already reviewed");
        }

        // 2. ตรวจสอบว่ามีเหตุผลการ reject
        if (adminComment == null || adminComment.isBlank()) {
            throw new RuntimeException("Admin comment is required for rejection");
        }

        // 3. อัปเดต WithdrawalRequest status เป็น REJECTED
        wr.setStatus(WithdrawStatus.REJECTED);
        wr.setAdminComment(adminComment);
        wr.setReviewedBy(adminId);
        wr.setReviewedAt(LocalDateTime.now(ZoneId.of("Asia/Bangkok")));
        withdrawalRequestRepository.save(wr);

        return new WithdrawalRejectResponse(
                "Withdrawal rejected successfully",
                withdrawalId,
                adminComment,
                WithdrawStatus.REJECTED);
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
