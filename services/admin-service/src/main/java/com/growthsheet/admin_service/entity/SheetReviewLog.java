package com.growthsheet.admin_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sheet_review_logs")
@Getter
@Setter
public class SheetReviewLog {

    @Id
    @GeneratedValue
    private UUID id;   // << ต้องมีตัวนี้

    @Column(name = "sheet_id", nullable = false)
    private UUID sheetId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(name = "admin_id", nullable = false)
    private UUID adminId;

    @Column(nullable = false)
    private String action; // APPROVED / REJECTED

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}