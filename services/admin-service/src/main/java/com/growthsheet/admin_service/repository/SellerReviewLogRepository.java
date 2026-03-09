package com.growthsheet.admin_service.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.growthsheet.admin_service.entity.SellerReviewLog;

public interface SellerReviewLogRepository extends JpaRepository<SellerReviewLog, UUID> {
}