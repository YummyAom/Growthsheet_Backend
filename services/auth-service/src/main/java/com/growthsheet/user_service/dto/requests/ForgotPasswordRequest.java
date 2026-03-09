package com.growthsheet.user_service.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(

    @NotBlank(message = "กรุณากรอก email")
    @Email(message = "รูปแบบ email ไม่ถูกต้อง")
    String email
) {}
