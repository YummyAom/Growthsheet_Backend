package com.growthsheet.payment_service.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.growthsheet.payment_service.entity.OrderItem;
import com.growthsheet.payment_service.dto.dashboard.SheetPerformanceProjection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    /**
     * คำนวณ net_revenue ของ seller:
     * - join order_items → sheets (filter seller_id)
     * - join order_items → orders → payments (filter status = 'PAID')
     * - SUM(order_items.price) × 0.85
     */
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

    /**
     * คำนวณยอดขายรวมทั้งหมดของ seller
     */
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

    /**
     * ดึงข้อมูลยอดขายและรายได้แยกตาม Sheet (หลังหัก 15%)
     */
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

}
