package com.growthsheet.payment_service.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.growthsheet.payment_service.dto.SellerBalanceResponse;
import com.growthsheet.payment_service.dto.WithdrawalHistoryDTO;
import com.growthsheet.payment_service.entity.WithdrawalRequest;
import com.growthsheet.payment_service.repository.OrderItemRepository;
import com.growthsheet.payment_service.repository.WithdrawalRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final OrderItemRepository orderItemRepository;

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
     *
     * net_revenue = SUM(order_items.price) × 0.85
     * withdrawn = SUM(withdrawal_requests.amount) WHERE status IN
     * ('APPROVED','PENDING')
     * available = net_revenue - withdrawn
     */
    public SellerBalanceResponse getSellerBalance(UUID sellerId) {
        // คำนวณ net_revenue (ยอดขายหลังหัก 15%)
        BigDecimal netRevenue = orderItemRepository.calculateNetRevenueBySellerId(sellerId);
        if (netRevenue == null) {
            netRevenue = BigDecimal.ZERO;
        }

        // คำนวณ withdrawn (ถอนไปแล้ว + รอโอน)
        BigDecimal withdrawn = withdrawalRequestRepository.sumWithdrawnBySellerId(sellerId);
        if (withdrawn == null) {
            withdrawn = BigDecimal.ZERO;
        }

        // คำนวณ available
        BigDecimal available = netRevenue.subtract(withdrawn);

        return new SellerBalanceResponse(netRevenue, withdrawn, available);
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
