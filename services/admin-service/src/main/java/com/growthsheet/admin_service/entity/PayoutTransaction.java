package com.growthsheet.admin_service.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payout_transactions")
@Getter
@Setter
@NoArgsConstructor
public class PayoutTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "withdrawal_id", nullable = false)
    private UUID withdrawalId;

    @Column(name = "admin_id")
    private UUID adminId;

    @Column(name = "slip_url", length = 255)
    private String slipUrl;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @Column(name = "transferred_at")
    private OffsetDateTime transferredAt;
}
