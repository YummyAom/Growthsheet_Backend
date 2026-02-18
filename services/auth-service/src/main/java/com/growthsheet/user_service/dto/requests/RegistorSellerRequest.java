package com.growthsheet.user_service.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * DTO สำหรับรับข้อมูลการลงทะเบียนผู้ขาย
 * ชื่อฟิลด์ตรงตาม JSON structure ที่กำหนดไว้
 */
@Builder
public record RegistorSellerRequest(
        @NotBlank(message = "กรุณาระบุนามปากกา") String nickname, // ตรงกับ nickname ใน JSON

        @NotBlank(message = "กรุณาระบุชื่อ-นามสกุลจริง") String fullName, // ตรงกับ fullName ใน JSON

        @NotBlank(message = "กรุณาระบุชื่อมหาวิทยาลัย") String university, // ตรงกับ university ใน JSON

        @NotBlank(message = "กรุณาระบุรหัสนักศึกษา") String studentId, // ตรงกับ studentId ใน JSON

        @NotBlank(message = "กรุณาระบุหมายเลขโทรศัพท์") String phone, // ตรงกับ phone ใน JSON

        // @NotBlank(message = "กรุณาระบุอีเมล") @Email(message = "รูปแบบอีเมลไม่ถูกต้อง") String email, // ตรงกับ email ใน
        //                                                                                               // JSON

        @NotBlank(message = "กรุณาระบุชื่อธนาคาร") String bankName, // ตรงกับ bankName ใน JSON

        @NotBlank(message = "กรุณาระบุเลขที่บัญชี") String bankAccountNumber, // ตรงกับ bankAccountNumber ใน JSON

        @NotBlank(message = "กรุณาระบุชื่อเจ้าของบัญชี") String bankAccountName // ตรงกับ bankAccountName ใน JSON
) {
}