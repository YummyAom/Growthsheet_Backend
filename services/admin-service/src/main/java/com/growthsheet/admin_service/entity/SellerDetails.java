
package com.growthsheet.admin_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seller_details")
@Getter
@Setter
public class SellerDetails {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "pen_name")
    private String penName;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "university")
    private String university;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "id_card_url")
    private String idCardUrl;

    @Column(name = "selfie_id_url")
    private String selfieIdUrl;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "bank_account_name")
    private String bankAccountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SellerStatus isVerified;

    @Column(name = "admin_comment")
    private String adminComment;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}