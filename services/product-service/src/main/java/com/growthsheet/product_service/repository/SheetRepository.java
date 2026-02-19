package com.growthsheet.product_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.entity.SheetStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SheetRepository extends JpaRepository<Sheet, UUID> {
        @EntityGraph(attributePaths = {
                        "university",
                        "category",
                        "hashtags",
                        "previewImages"
        })
        // @EntityGraph(attributePaths = { "seller" })
        Page<Sheet> findByStatus(SheetStatus status, Pageable pageable);

        Page<Sheet> findAllBySellerId(UUID sellerId, Pageable pageable);

        Page<Sheet> findAllBySellerIdAndIsPublished(
                        UUID sellerId,
                        Boolean isPublished,
                        Pageable pageable);

        @Query("""
                            SELECT s
                            FROM Sheet s
                            JOIN SheetLike sl ON s.id = sl.sheetId
                            WHERE sl.userId = :userId
                        """)
        Page<Sheet> findLikedSheets(@Param("userId") UUID userId, Pageable pageable);

}