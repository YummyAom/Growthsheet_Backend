package com.growthsheet.payment_service.dto;

import java.util.UUID;

// ใช้ record จะสั้นและง่ายที่สุดสำหรับรับ data
public record ChargeRequest(UUID orderId) {}