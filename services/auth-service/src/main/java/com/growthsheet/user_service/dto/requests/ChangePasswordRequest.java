package com.growthsheet.user_service.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(

    @NotBlank(message = "กรุณากรอกรหัสผ่านเดิม")
    String oldPassword,

    @NotBlank(message = "กรุณากรอกรหัสผ่านใหม่")
    String newPassword,

    @NotBlank(message = "กรุณายืนยันรหัสผ่านใหม่")
    String confirmNewPassword
) {}
