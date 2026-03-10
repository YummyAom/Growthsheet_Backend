package com.growthsheet.payment_service.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.growthsheet.payment_service.entity.OrderItem;
import com.growthsheet.payment_service.dto.dashboard.SheetPerformanceProjection;
import com.growthsheet.payment_service.dto.dashboard.DailySalesProjection;
import com.growthsheet.payment_service.dto.dashboard.MonthlySalesProjection;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    @Query(value = """
                SELECT COALESCE(SUM(oi.price) * 0.85, 0)
                FROM order_items oi
                JOIN sheets s ON oi.sheet_id = s.id
                JOIN orders o ON oi.order_id = o.id
                JOIN payments p ON p.order_id = o.id
                WHERE s.seller_id = :sellerId
                AND p.status = 'PAID'
            """, nativeQuery = true)
    BigDecimal calculateNetRevenueBySellerId(@Param("sellerId") UUID sellerId);

    @Query(value = """
                SELECT COALESCE(COUNT(oi.id), 0)
                FROM order_items oi
                JOIN sheets s ON oi.sheet_id = s.id
                JOIN orders o ON oi.order_id = o.id
                JOIN payments p ON p.order_id = o.id
                WHERE s.seller_id = :sellerId
                AND p.status = 'PAID'
            """, nativeQuery = true)
    Long calculateTotalSalesVolumeBySellerId(@Param("sellerId") UUID sellerId);

    @Query(value = """
                SELECT
                    oi.sheet_id as sheetId,
                    oi.sheet_name as sheetName,
                    COUNT(oi.id) as salesVolume,
                    COALESCE(SUM(oi.price) * 0.85, 0) as totalRevenue
                FROM order_items oi
                JOIN sheets s ON oi.sheet_id = s.id
                JOIN orders o ON oi.order_id = o.id
                JOIN payments p ON p.order_id = o.id
                WHERE s.seller_id = :sellerId
                AND p.status = 'PAID'
                GROUP BY oi.sheet_id, oi.sheet_name
                ORDER BY salesVolume DESC
            """, nativeQuery = true)
    List<SheetPerformanceProjection> getSheetPerformanceBySellerId(@Param("sellerId") UUID sellerId);

    /**
     * ยอดขายวันนี้ (หลังหัก 15%)
     */
    @Query(value = """
                SELECT COALESCE(SUM(oi.price) * 0.85, 0)
                FROM order_items oi
                JOIN sheets s ON oi.sheet_id = s.id
                JOIN orders o ON oi.order_id = o.id
                JOIN payments p ON p.order_id = o.id
                WHERE s.seller_id = :sellerId
                AND p.status = 'PAID'
                AND DATE(o.created_at) = CURRENT_DATE
            """, nativeQuery = true)
    BigDecimal calculateTodaySalesBySellerId(@Param("sellerId") UUID sellerId);

    /**
     * ยอดขายรายวัน 7 วันย้อนหลัง (หลังหัก 15%)
     */
    @Query(value = """
                SELECT
                    DATE(o.created_at) AS saleDate,
                    COALESCE(SUM(oi.price) * 0.85, 0) AS totalAmount
                FROM order_items oi
                JOIN sheets s ON oi.sheet_id = s.id
                JOIN orders o ON oi.order_id = o.id
                JOIN payments p ON p.order_id = o.id
                WHERE s.seller_id = :sellerId
                AND p.status = 'PAID'
                AND o.created_at >= :since
                GROUP BY DATE(o.created_at)
                ORDER BY saleDate ASC
            """, nativeQuery = true)
    List<DailySalesProjection> getWeeklySalesBySellerId(
            @Param("sellerId") UUID sellerId,
            @Param("since") LocalDateTime since);

    /**
     * ยอดขายรายเดือน 6 เดือนย้อนหลัง (หลังหัก 15%)
     */
    @Query(value = """
                SELECT
                    EXTRACT(YEAR FROM o.created_at)::int  AS year,
                    EXTRACT(MONTH FROM o.created_at)::int AS month,
                    COALESCE(SUM(oi.price) * 0.85, 0)    AS totalAmount
                FROM order_items oi
                JOIN sheets s ON oi.sheet_id = s.id
                JOIN orders o ON oi.order_id = o.id
                JOIN payments p ON p.order_id = o.id
                WHERE s.seller_id = :sellerId
                AND p.status = 'PAID'
                AND o.created_at >= :since
                GROUP BY EXTRACT(YEAR FROM o.created_at), EXTRACT(MONTH FROM o.created_at)
                ORDER BY year ASC, month ASC
            """, nativeQuery = true)
    List<MonthlySalesProjection> getMonthlySalesBySellerId(
            @Param("sellerId") UUID sellerId,
            @Param("since") LocalDateTime since);
}