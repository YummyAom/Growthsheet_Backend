package com.growthsheet.payment_service.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.growthsheet.payment_service.entity.RefundRequest;
import com.growthsheet.payment_service.entity.RefundStatus;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, UUID> {
    List<RefundRequest> findByUserId(UUID userId);
    List<RefundRequest> findByStatus(RefundStatus status);
    boolean existsByOrderItemIdAndStatus(UUID orderItemId, RefundStatus status);
}
