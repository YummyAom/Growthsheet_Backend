package com.growthsheet.user_service.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(

    @NotBlank(message = "กรุณากรอก email")
    @Email(message = "รูปแบบ email ไม่ถูกต้อง")
    String email,

    @NotBlank(message = "กรุณากรอก OTP")
    String otp,

    @NotBlank(message = "กรุณากรอกรหัสผ่านใหม่")
    String newPassword,

    @NotBlank(message = "กรุณายืนยันรหัสผ่านใหม่")
    String confirmNewPassword
) {}
