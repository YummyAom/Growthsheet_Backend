package com.growthsheet.admin_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.admin_service.entity.SheetReviewLog;

public interface SheetReviewLogRepository
        extends JpaRepository<SheetReviewLog, UUID> {

    Optional<SheetReviewLog> findTopBySheetIdOrderByCreatedAtDesc(UUID sheetId);
}