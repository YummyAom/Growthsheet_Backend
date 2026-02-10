package com.growthsheet.product_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.product_service.entity.SheetReview;

public interface ReviewRepository extends JpaRepository<SheetReview, UUID>{
    boolean existsBySheetIdAndUserId(UUID sheetId, UUID userId);

    long countByUserId(UUID userId);

    List<SheetReview> findBySheetId(UUID sheetId);
}
