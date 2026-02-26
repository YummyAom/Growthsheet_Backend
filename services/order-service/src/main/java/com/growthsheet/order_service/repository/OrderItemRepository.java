package com.growthsheet.order_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.order_service.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID>{
    List<OrderItem> findByOrderId(UUID orderId);
    
    // boolean existsByOrderUserIdAndSheetIdAndOrderStatus(UUID userId, UUID sheetId, String status);

    List<OrderItem> findByOrderUserIdAndSheetId(UUID userId, UUID sheetId);
}
