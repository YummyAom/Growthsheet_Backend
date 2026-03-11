package com.growthsheet.payment_service.service;

import com.growthsheet.payment_service.dto.ApproveRefundDto;
import com.growthsheet.payment_service.dto.CreateRefundRequestDto;
import com.growthsheet.payment_service.dto.RefundResponseDto;
import com.growthsheet.payment_service.dto.RejectRefundDto;
import com.growthsheet.payment_service.entity.OrderItem;
import com.growthsheet.payment_service.entity.RefundRequest;
import com.growthsheet.payment_service.entity.RefundStatus;
import com.growthsheet.payment_service.repository.OrderItemRepository;
import com.growthsheet.payment_service.repository.PaymentRepository;
import com.growthsheet.payment_service.repository.RefundRequestRepository;
import com.growthsheet.payment_service.config.client.OrderClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.growthsheet.payment_service.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RefundService {

    private final RefundRequestRepository refundRepo;
    private final OrderItemRepository orderItemRepo;
    private final OrderClient orderClient;
    private final PaymentRepository paymentRepo;

    public RefundService(RefundRequestRepository refundRepo,
            OrderItemRepository orderItemRepo,
            PaymentRepository paymentRepo,
            OrderClient orderClient) {
        this.refundRepo = refundRepo;
        this.orderItemRepo = orderItemRepo;
        this.paymentRepo = paymentRepo;
        this.orderClient = orderClient;
    }

    @Transactional
    public RefundResponseDto createRefundRequest(UUID userId, CreateRefundRequestDto req) {

        OrderItem orderItem = orderItemRepo.findById(req.getOrderItemId())
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        // 1. เช็คข้อมูล Payment และตรวจสอบความเป็นเจ้าของ (Validate user)
        Payment payment = paymentRepo.findByOrderId(orderItem.getOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found for this order"));

        if (!payment.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: This order item does not belong to you.");
        }

        // 2. เช็คเงื่อนไขเวลา: ห้ามเกิน 7 วัน นับจากวันที่ชำระเงินสำเร็จ (updatedAt)
        LocalDateTime limitDate = payment.getUpdatedAt().plusDays(7);
        if (LocalDateTime.now().isAfter(limitDate)) {
            throw new RuntimeException("Refund period has expired (exceeds 7 days).");
        }

        // 3. ป้องกันการขอคืนเงินซ้ำซ้อน
        if (Boolean.TRUE.equals(orderItem.getIsRefunded())) {
            throw new RuntimeException("This item has already been refunded.");
        }

        // (Optional) ตรวจสอบว่ามีคำขอ PENDING ค้างอยู่แล้วหรือไม่
        boolean hasPendingRequest = refundRepo.existsByOrderItemIdAndStatus(req.getOrderItemId(), RefundStatus.PENDING);
        if (hasPendingRequest) {
            throw new RuntimeException("A refund request for this item is already pending.");
        }

        // 4. สร้างคำขอคืนเงิน
        RefundRequest refund = new RefundRequest();
        refund.setOrderItemId(req.getOrderItemId());
        refund.setUserId(userId);
        refund.setReason(req.getReason());
        refund.setEvidenceUrl(req.getEvidenceUrl());
        refund.setBankAccountName(req.getBankAccountName());
        refund.setBankAccountNumber(req.getBankAccountNumber());
        refund.setBankName(req.getBankName());
        refund.setStatus(RefundStatus.PENDING);

        RefundRequest saved = refundRepo.save(refund);

        return mapToDto(saved);
    }

    public List<RefundResponseDto> getRefundsByUser(UUID userId) {
        return refundRepo.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<RefundResponseDto> getPendingRefunds() {
        return refundRepo.findByStatus(RefundStatus.PENDING).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RefundResponseDto approveRefund(UUID refundId, UUID adminId, ApproveRefundDto req) {
        RefundRequest refund = refundRepo.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund request not found"));

        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new RuntimeException("Refund is not PENDING");
        }

        refund.setStatus(RefundStatus.REFUNDED);
        refund.setAdminId(adminId);
        refund.setRefundSlipUrl(req.getRefundSlipUrl());
        refund.setAdminComment(req.getAdminComment());
        RefundRequest saved = refundRepo.save(refund);

        // Update OrderItem in payment-service
        OrderItem item = orderItemRepo.findById(refund.getOrderItemId())
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
        item.setIsRefunded(true);
        orderItemRepo.save(item);

        // Notify Order Service to revoke access
        try {
            orderClient.revokeOrderItemAccess(refund.getOrderItemId());
        } catch (Exception e) {
            System.err.println("Failed to revoke access in order-service: " + e.getMessage());
            // Depending on architecture, might throw or retry
        }

        return mapToDto(saved);
    }

    @Transactional
    public RefundResponseDto rejectRefund(UUID refundId, UUID adminId, RejectRefundDto req) {
        RefundRequest refund = refundRepo.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund request not found"));

        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new RuntimeException("Refund is not PENDING");
        }

        refund.setStatus(RefundStatus.REJECTED);
        refund.setAdminId(adminId);
        refund.setAdminComment(req.getAdminComment());
        RefundRequest saved = refundRepo.save(refund);

        return mapToDto(saved);
    }

    public RefundResponseDto mapToDto(RefundRequest req) {
        RefundResponseDto dto = new RefundResponseDto();
        dto.setId(req.getId());
        dto.setOrderItemId(req.getOrderItemId());
        dto.setUserId(req.getUserId());
        dto.setReason(req.getReason());
        dto.setEvidenceUrl(req.getEvidenceUrl());
        dto.setBankAccountName(req.getBankAccountName());
        dto.setBankAccountNumber(req.getBankAccountNumber());
        dto.setBankName(req.getBankName());
        dto.setStatus(req.getStatus());
        dto.setRefundSlipUrl(req.getRefundSlipUrl());
        dto.setAdminId(req.getAdminId());
        dto.setAdminComment(req.getAdminComment());
        dto.setCreatedAt(req.getCreatedAt());
        dto.setUpdatedAt(req.getUpdatedAt());
        return dto;
    }
}
