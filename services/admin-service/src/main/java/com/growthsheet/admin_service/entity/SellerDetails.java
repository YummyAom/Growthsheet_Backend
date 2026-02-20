
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

@Getter
@Setter
@Entity
@Table(name = "seller_details")
public class SellerDetails {

    @Id
    @Column(name = "user_id")
    private UUID user_id;

    @Column(name = "pen_name")
    private String pen_name;

    @Column(name = "full_name")
    private String full_name;

    @Column(name = "university")
    private String university;

    @Column(name = "student_id")
    private String student_id;

    @Column(name = "id_card_url")
    private String id_card_url;

    @Column(name = "selfie_id_url")
    private String selfie_id_url;

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "bank_name")
    private String bank_name;

    @Column(name = "bank_account_number")
    private String bank_account_number;

    @Column(name = "bank_account_name")
    private String bank_account_name;

    // PENDING / APPROVED / REJECTED
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SellerStatus is_verified;

    @Column(name = "admin_comment")
    private String admin_comment;

    @Column(name = "reviewed_by")
    private UUID reviewed_by;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewed_at;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;
}
