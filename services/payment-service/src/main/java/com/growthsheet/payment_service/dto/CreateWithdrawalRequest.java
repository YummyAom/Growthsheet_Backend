package com.growthsheet.payment_service.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO สำหรับสร้างคำขอถอนเงินของ seller
 * User แค่กรอกจำนวนเงินอย่างเดียว ข้อมูลธนาคารดึงจาก seller_details อัตโนมัติ
 */
@Getter
@Setter
public class CreateWithdrawalRequest {
    private BigDecimal amount;
}
