package com.growthsheet.product_service.repository;

import java.util.UUID;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.entity.SheetStatus;

public interface SheetRepository extends JpaRepository<Sheet, UUID> {

    @EntityGraph(attributePaths = {
            "university",
            "category"
    })
    Page<Sheet> findByIdIn(Set<UUID> ids, Pageable pageable);

    @EntityGraph(attributePaths = {
            "university",
            "category",
            "hashtags",
            "previewImages"
    })
    Page<Sheet> findByIsPublishedTrue(Pageable pageable);

    @EntityGraph(attributePaths = {
            "university",
            "category",
            "hashtags",
            "previewImages"
    })
    Page<Sheet> findByStatusAndIsPublishedTrue(
            SheetStatus status,
            Pageable pageable);

    @EntityGraph(attributePaths = {
            "university",
            "category",
            "hashtags",
            "previewImages"
    })
    Page<Sheet> findByStatusAndIsPublished(
            SheetStatus status,
            Boolean isPublished,
            Pageable pageable);

    @EntityGraph(attributePaths = {
            "university",
            "category",
            "hashtags",
            "previewImages"
    })
    Page<Sheet> findByIsPublishedFalse(Pageable pageable);

    @EntityGraph(attributePaths = {
            "university",
            "category",
            "hashtags",
            "previewImages"
    })
    Page<Sheet> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {
            "university",
            "category",
            "hashtags",
            "previewImages"
    })
    Page<Sheet> findByStatus(SheetStatus status, Pageable pageable);

    Page<Sheet> findAllBySellerId(UUID sellerId, Pageable pageable);

    Page<Sheet> findAllBySellerIdAndIsPublished(
            UUID sellerId,
            Boolean isPublished,
            Pageable pageable);

    /**
     * ดึง sheets ตาม sellerId และ status (เช่น PENDING, APPROVED, REJECTED)
     */
    Page<Sheet> findAllBySellerIdAndStatus(
            UUID sellerId,
            SheetStatus status,
            Pageable pageable);

    @Query("""
                SELECT s
                FROM Sheet s
                JOIN SheetLike sl ON s.id = sl.sheetId
                WHERE sl.userId = :userId
            """)
    Page<Sheet> findLikedSheets(@Param("userId") UUID userId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "university",
            "category",
            "hashtags",
            "previewImages"
    })
    // เพิ่ม Method นี้เพื่อรองรับการกรองหลาย Status พร้อมกับ isPublished
    Page<Sheet> findByStatusInAndIsPublished(
            java.util.List<SheetStatus> statuses,
            Boolean isPublished,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT s
            FROM Sheet s
            JOIN s.hashtags h
            WHERE LOWER(h.name) = LOWER(:tag)
            """)
    Page<Sheet> findSheetsByTag(
            @Param("tag") String tag,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT s
            FROM Sheet s
            JOIN s.hashtags h
            WHERE LOWER(h.name) = LOWER(:tag)
            AND s.status IN :statuses
            AND s.isPublished = true
            """)
    Page<Sheet> findPublishedSheetsByTag(
            String tag,
            List<SheetStatus> statuses,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT s
            FROM Sheet s
            JOIN s.hashtags h
            WHERE LOWER(h.name) = LOWER(:tag)
            AND s.status IN :statuses
            AND s.isPublished = false
            """)
    Page<Sheet> findUnpublishedSheetsByTag(
            String tag,
            List<SheetStatus> statuses,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT s
            FROM Sheet s
            JOIN s.hashtags h
            WHERE h.name IN :tags
            """)
    Page<Sheet> findByTags(List<String> tags, Pageable pageable);

    @Query("""
            SELECT DISTINCT s
            FROM Sheet s
            JOIN s.hashtags h
            WHERE h.name IN :tags
            """)
    Page<Sheet> findSheetsByTags(
            List<String> tags,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT s
            FROM Sheet s
            JOIN s.hashtags h
            WHERE h.name IN :tags
            AND s.status IN :statuses
            AND s.isPublished = true
            """)
    Page<Sheet> findPublishedSheetsByTags(
            List<String> tags,
            List<SheetStatus> statuses,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT s
            FROM Sheet s
            JOIN s.hashtags h
            WHERE h.name IN :tags
            AND s.status IN :statuses
            AND s.isPublished = false
            """)
    Page<Sheet> findUnpublishedSheetsByTags(
            List<String> tags,
            List<SheetStatus> statuses,
            Pageable pageable);
}
