package com.growthsheet.product_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.entity.SheetStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SheetRepository extends JpaRepository<Sheet, UUID> {

    Page<Sheet> findByStatus(SheetStatus status, Pageable pageable);
}