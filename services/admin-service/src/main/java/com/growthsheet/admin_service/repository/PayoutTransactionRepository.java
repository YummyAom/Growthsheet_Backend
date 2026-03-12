package com.growthsheet.admin_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.admin_service.entity.PayoutTransaction;

public interface PayoutTransactionRepository extends JpaRepository<PayoutTransaction, UUID> {
    Optional<PayoutTransaction> findByWithdrawalId(UUID withdrawalId);
}
