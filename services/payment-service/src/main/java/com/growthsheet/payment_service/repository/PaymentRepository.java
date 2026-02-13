package com.growthsheet.payment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.payment_service.dto.PaymentStatus;
import com.growthsheet.payment_service.entity.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByChargeId(String chargeId);

    Optional<Payment> findByOrderId(UUID orderId);

    Optional<Payment> findByOrderIdAndStatus(UUID orderId, PaymentStatus status);
}