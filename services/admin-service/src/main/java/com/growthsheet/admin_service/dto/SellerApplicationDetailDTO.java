package com.growthsheet.admin_service.dto;


import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerApplicationDetailDTO {

    private UUID user_id;

    private String pen_name;
    private String full_name;
    private String university;
    private String student_id;

    private String id_card_url;
    private String selfie_id_url;

    private String phone_number;

    private String bank_name;
    private String bank_account_number;
    private String bank_account_name;

    private String is_verified;   // PENDING / APPROVED / REJECTED
    private String admin_comment;

    private UUID reviewed_by;
    private LocalDateTime reviewed_at;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}