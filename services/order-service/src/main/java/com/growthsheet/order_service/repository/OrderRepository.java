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
}
