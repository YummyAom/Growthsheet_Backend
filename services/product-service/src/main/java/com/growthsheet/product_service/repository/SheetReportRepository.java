package com.growthsheet.product_service.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.product_service.entity.ReportStatus;
import com.growthsheet.product_service.entity.SheetReport;

public interface SheetReportRepository extends JpaRepository<SheetReport, UUID> {

    // ตรวจสอบว่า user เคย report sheet นี้ไปแล้วหรือยัง
    boolean existsBySheetIdAndReporterId(UUID sheetId, UUID reporterId);

    // ดึง report ทั้งหมดตาม status (สำหรับ admin)
    Page<SheetReport> findByStatus(ReportStatus status, Pageable pageable);

    // ดึง report ทั้งหมดของ sheet นั้น
    Page<SheetReport> findBySheetId(UUID sheetId, Pageable pageable);

    // นับจำนวน report ของ sheet นั้น
    long countBySheetId(UUID sheetId);
}
