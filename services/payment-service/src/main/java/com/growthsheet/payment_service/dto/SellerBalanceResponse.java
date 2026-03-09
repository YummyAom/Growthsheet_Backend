package com.growthsheet.payment_service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO สำหรับแสดงยอดเงินที่ถอนได้ของ seller
 *
 * net_revenue = SUM(order_items.price) × 0.85 (ยอดขายหลังหัก 15%)
 * withdrawn = SUM(withdrawal_requests.amount) WHERE status IN
 * ('APPROVED','PENDING')
 * available = net_revenue - withdrawn
 */
@Getter
@Setter
@AllArgsConstructor
public class SellerBalanceResponse {

    private BigDecimal netRevenue; // ยอดขายหลังหัก 15%
    private BigDecimal withdrawn; // ถอนไปแล้ว + รอโอน
    private BigDecimal available; // ยอดที่ถอนได้
}
