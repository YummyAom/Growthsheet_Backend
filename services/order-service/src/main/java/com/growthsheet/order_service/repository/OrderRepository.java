package com.growthsheet.order_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.order_service.entity.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {}
