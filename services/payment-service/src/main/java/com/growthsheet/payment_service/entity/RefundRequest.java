package com.growthsheet.payment_service.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "refund_requests")
@Getter
@Setter
public class RefundRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "order_item_id", nullable = false)
    private UUID orderItemId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "reason", columnDefinition = "text", nullable = false)
    private String reason;

    @Column(name = "evidence_url")
    private String evidenceUrl;

    @Column(name = "bank_account_name", nullable = false)
    private String bankAccountName;

    @Column(name = "bank_account_number", nullable = false)
    private String bankAccountNumber;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RefundStatus status = RefundStatus.PENDING;

    @Column(name = "refund_slip_url")
    private String refundSlipUrl;

    @Column(name = "admin_id")
    private UUID adminId;

    @Column(name = "admin_comment", columnDefinition = "text")
    private String adminComment;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
