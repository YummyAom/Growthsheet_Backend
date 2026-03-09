package com.growthsheet.product_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.growthsheet.product_service.entity.SheetReview;

public interface ReviewRepository extends JpaRepository<SheetReview, UUID> {
    boolean existsBySheetIdAndUserId(UUID sheetId, UUID userId);

    long countByUserId(UUID userId);

    List<SheetReview> findBySheetId(UUID sheetId);

    @Query("SELECT AVG(r.rating) FROM SheetReview r WHERE r.sheetId = :sheetId")
    Double getAverageRatingBySheetId(UUID sheetId);

    @Query("SELECT COUNT(r) FROM SheetReview r WHERE r.sheetId = :sheetId")
    Long countBySheetId(UUID sheetId);
}
