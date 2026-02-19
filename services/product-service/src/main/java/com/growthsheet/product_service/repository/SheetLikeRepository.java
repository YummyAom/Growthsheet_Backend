package com.growthsheet.product_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.product_service.entity.SheetLike;

import java.util.UUID;

public interface SheetLikeRepository extends JpaRepository<SheetLike, UUID> {

    boolean existsBySheetIdAndUserId(UUID sheetId, UUID userId);

    long countBySheetId(UUID sheetId);

    Page<SheetLike> findByUserId(UUID userId, Pageable pageable);

    void deleteBySheetIdAndUserId(UUID sheetId, UUID userId);
}