package com.growthsheet.product_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.product_service.entity.SheetImage;

public interface SheetImageRepository extends JpaRepository<SheetImage, UUID>{
    Optional<SheetImage> findFirstBySheetIdOrderBySortOrderAsc(UUID userId);    
}
