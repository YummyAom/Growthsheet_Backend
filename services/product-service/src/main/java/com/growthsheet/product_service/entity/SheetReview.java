package com.growthsheet.product_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sheet_reviews")
public class SheetReview {

    @Id
    private UUID id;

    @Column(name = "sheet_id")
    private UUID sheetId;

    @Column(name = "user_id")
    private UUID userId;

    private Integer rating;
    private String comment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}