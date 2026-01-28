package com.growthsheet.user_service.dto.requests;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;

public record RegisterRequest(

    @NotBlank(message = "username ห้ามว่าง")
    String username,

    @NotBlank(message = "email ห้ามว่าง")
    @Email(message = "email ไม่ถูกต้อง")
    String email,

    @NotBlank(message = "password ห้ามว่าง")
    String password,

    @NotBlank(message = "secPassword ห้ามว่าง")
    String secPassword
) {}
