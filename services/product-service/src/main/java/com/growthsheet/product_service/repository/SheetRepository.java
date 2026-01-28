package com.growthsheet.product_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.product_service.entity.Sheet;

public interface SheetRepository extends JpaRepository<Sheet, UUID> {
}
