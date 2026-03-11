package com.growthsheet.payment_service.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "price", nullable = false, precision = 38, scale = 2)
    private BigDecimal price;

    @Column(name = "seller_name")
    private String sellerName;

    @Column(name = "sheet_id")
    private UUID sheetId;

    @Column(name = "sheet_name")
    private String sheetName;

    @Column(name = "is_refunded")
    private Boolean isRefunded = false;
}
