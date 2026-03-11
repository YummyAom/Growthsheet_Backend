package com.growthsheet.order_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.growthsheet.order_service.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(UUID userId);

    List<Order> findByUserIdAndStatus(UUID userId, String status);

    Page<Order> findByUserIdAndStatus(UUID userId, String status, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.userId = :userId AND i.sheetId = :sheetId AND o.status = :status AND (i.isRefunded = false OR i.isRefunded IS NULL)")
    boolean existsByUserIdAndItemsSheetIdAndStatus(
            @org.springframework.data.repository.query.Param("userId") UUID userId,
            @org.springframework.data.repository.query.Param("sheetId") UUID sheetId,
            @org.springframework.data.repository.query.Param("status") String status);
}
