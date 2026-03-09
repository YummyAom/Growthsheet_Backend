package com.growthsheet.payment_service.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.growthsheet.payment_service.config.client.UserClient;
import com.growthsheet.payment_service.dto.CreateWithdrawalRequest;
import com.growthsheet.payment_service.dto.SellerBalanceResponse;
import com.growthsheet.payment_service.dto.WithdrawalHistoryDTO;
import com.growthsheet.payment_service.entity.WithdrawStatus;
import com.growthsheet.payment_service.entity.WithdrawalRequest;
import com.growthsheet.payment_service.repository.OrderItemRepository;
import com.growthsheet.payment_service.repository.WithdrawalRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserClient userClient;

    /**
     * ดูประวัติการถอนเงินของ seller (แบบ pagination)
     */
    public Page<WithdrawalHistoryDTO> getWithdrawalHistory(UUID sellerId, Pageable pageable) {
        return withdrawalRequestRepository
                .findBySellerIdOrderByCreatedAtDesc(sellerId, pageable)
                .map(this::toDTO);
    }

    /**
     * คำนวณยอดเงินที่ถอนได้ของ seller
     */
    public SellerBalanceResponse getSellerBalance(UUID sellerId) {
        BigDecimal netRevenue = orderItemRepository.calculateNetRevenueBySellerId(sellerId);
        if (netRevenue == null) {
            netRevenue = BigDecimal.ZERO;
        }

        BigDecimal withdrawn = withdrawalRequestRepository.sumWithdrawnBySellerId(sellerId);
        if (withdrawn == null) {
            withdrawn = BigDecimal.ZERO;
        }

        BigDecimal available = netRevenue.subtract(withdrawn);

        return new SellerBalanceResponse(netRevenue, withdrawn, available);
    }

    /**
     * สร้างคำขอถอนเงินใหม่
     * - User ส่งแค่ amount
     * - ข้อมูลธนาคารดึงจาก seller_details ผ่าน user-service อัตโนมัติ
     */
    public WithdrawalHistoryDTO createWithdrawalRequest(UUID sellerId, CreateWithdrawalRequest req) {
        // เช็คจำนวนเงิน
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "จำนวนเงินต้องมากกว่า 0");
        }

        // เช็คยอดเงินที่ถอนได้
        SellerBalanceResponse balance = getSellerBalance(sellerId);

        if (req.getAmount().compareTo(balance.getAvailable()) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ยอดเงินไม่เพียงพอ (ถอนได้สูงสุด " + balance.getAvailable() + " บาท)");
        }

        // ดึงข้อมูลธนาคารจาก user-service (seller_details)
        Map<String, String> bankInfo = userClient.getSellerBankInfo(sellerId);

        // สร้าง entity ใหม่
        WithdrawalRequest wr = new WithdrawalRequest();
        wr.setSellerId(sellerId);
        wr.setUserId(sellerId);
        wr.setAmount(req.getAmount());
        wr.setStatus(WithdrawStatus.PENDING);
        wr.setBankName(bankInfo.get("bankName"));
        wr.setBankAccountNumber(bankInfo.get("bankAccountNumber"));
        wr.setBankAccountName(bankInfo.get("bankAccountName"));
        wr.setCreatedAt(OffsetDateTime.now());
        wr.setUpdatedAt(OffsetDateTime.now());

        WithdrawalRequest saved = withdrawalRequestRepository.save(wr);

        return toDTO(saved);
    }

    private WithdrawalHistoryDTO toDTO(WithdrawalRequest wr) {
        WithdrawalHistoryDTO dto = new WithdrawalHistoryDTO();
        dto.setId(wr.getId());
        dto.setAmount(wr.getAmount());
        dto.setStatus(wr.getStatus().name());
        dto.setBankName(wr.getBankName());
        dto.setBankAccountNumber(wr.getBankAccountNumber());
        dto.setBankAccountName(wr.getBankAccountName());
        dto.setNote(wr.getNote());
        dto.setAdminComment(wr.getAdminComment());
        dto.setCreatedAt(wr.getCreatedAt());
        dto.setUpdatedAt(wr.getUpdatedAt());
        return dto;
    }
}


