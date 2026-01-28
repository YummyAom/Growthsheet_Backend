package com.growthsheet.user_service.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

    @NotBlank(message = "email ห้ามว่าง")
    @Email(message = "email ไม่ถูกต้อง")
    String email,

    @NotBlank(message = "password ห้ามว่าง")
    String password

) {}
