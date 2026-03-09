package com.growthsheet.payment_service.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity สำหรับ join กับ order_items เพื่อหา seller_id
 * ใช้เฉพาะ field ที่จำเป็นเท่านั้น (read-only)
 */
@Entity
@Table(name = "sheets")
@Getter
@Setter
public class SheetRef {

    @Id
    private UUID id;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;
}
