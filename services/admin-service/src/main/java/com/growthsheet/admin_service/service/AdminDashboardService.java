package com.growthsheet.admin_service.service;

import com.growthsheet.admin_service.dto.dashboard.DashboardDTOs;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final JdbcTemplate jdbcTemplate;

    public DashboardDTOs.DashboardSummaryResponse getSummary(UUID adminId) {
        Long totalUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        Long totalSellers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM seller_details", Long.class);
        Long totalSheets = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sheets", Long.class);

        // จุดที่ 1: แก้เหลือแค่ 'PAID'
        BigDecimal totalRevenue = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(total_price), 0) FROM orders WHERE status = 'PAID'",
                BigDecimal.class
        );

        Long pendingReports = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sheet_reports WHERE status = 'PENDING'", Long.class);
        Long pendingWithdrawals = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM withdrawal_requests WHERE status = 'PENDING'", Long.class);

        return DashboardDTOs.DashboardSummaryResponse.builder()
                .totalUsers(totalUsers)
                .totalSellers(totalSellers)
                .totalSheets(totalSheets)
                .totalRevenue(totalRevenue)
                .pendingReports(pendingReports)
                .pendingWithdrawals(pendingWithdrawals)
                .build();
    }

    public DashboardDTOs.RevenueChartResponse getRevenueChart(UUID adminId, String range) {
        int days = parseRange(range);
        String sql = """
                SELECT DATE(created_at) as date, COALESCE(SUM(total_price), 0) as revenue
                FROM orders
                WHERE created_at >= CURRENT_DATE - (? || ' days')::interval
                AND status = 'PAID'
                GROUP BY DATE(created_at)
                ORDER BY DATE(created_at) ASC;
                """;

        List<DashboardDTOs.RevenueData> data = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> DashboardDTOs.RevenueData.builder()
                        .date(rs.getString("date"))
                        .revenue(rs.getBigDecimal("revenue"))
                        .build(),
                days
        );

        return DashboardDTOs.RevenueChartResponse.builder()
                .range(range)
                .data(data)
                .build();
    }

    public DashboardDTOs.UserGrowthResponse getUserGrowth(UUID adminId, String range) {
        int days = parseRange(range);
        String sql = """
                SELECT DATE(created_at) as date, COUNT(*) as users
                FROM users
                WHERE created_at >= CURRENT_DATE - (? || ' days')::interval
                GROUP BY DATE(created_at)
                ORDER BY DATE(created_at) ASC;
                """;

        List<DashboardDTOs.UserGrowthData> data = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> DashboardDTOs.UserGrowthData.builder()
                        .date(rs.getString("date"))
                        .users(rs.getLong("users"))
                        .build(),
                days
        );

        return DashboardDTOs.UserGrowthResponse.builder()
                .data(data)
                .build();
    }

    public List<DashboardDTOs.TopSellerResponse> getTopSellers(UUID adminId, int limit) {
        // จุดที่ 2: ใช้ Query ที่ดึง seller_name จาก order_items โดยตรง และเช็คสถานะเป็น 'PAID'
        String sql = """
                SELECT oi.seller_name as seller_name,
                       COUNT(oi.id) as sales,
                       COALESCE(SUM(oi.price), 0) as revenue
                FROM order_items oi
                JOIN orders o ON oi.order_id = o.id
                WHERE o.status = 'PAID'
                GROUP BY oi.seller_name
                ORDER BY revenue DESC
                LIMIT ?;
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> DashboardDTOs.TopSellerResponse.builder()
                        .sellerId(UUID.randomUUID()) // ใส่ค่าสุ่มให้ครบโครงสร้าง DTO
                        .sellerName(rs.getString("seller_name"))
                        .sales(rs.getLong("sales"))
                        .revenue(rs.getBigDecimal("revenue"))
                        .build(),
                limit
        );
    }

    public List<DashboardDTOs.TopSheetResponse> getTopSheets(UUID adminId, int limit) {
        // จุดที่ 3: แก้เหลือแค่ 'PAID'
        String sql = """
                SELECT oi.sheet_id as sheet_id,
                       oi.sheet_name as title,
                       COUNT(oi.id) as sales
                FROM order_items oi
                JOIN orders o ON oi.order_id = o.id
                WHERE o.status = 'PAID'
                GROUP BY oi.sheet_id, oi.sheet_name
                ORDER BY sales DESC
                LIMIT ?;
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> DashboardDTOs.TopSheetResponse.builder()
                        .sheetId((UUID) rs.getObject("sheet_id"))
                        .title(rs.getString("title"))
                        .sales(rs.getLong("sales"))
                        .build(),
                limit
        );
    }

    public DashboardDTOs.PendingActionsResponse getPendingActions(UUID adminId) {
        Long reports = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sheet_reports WHERE status = 'PENDING'", Long.class);
        Long withdrawRequests = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM withdrawal_requests WHERE status = 'PENDING'", Long.class);
        Long sellerApplications = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM seller_details WHERE is_verified = 'PENDING'", Long.class);

        return DashboardDTOs.PendingActionsResponse.builder()
                .reports(reports)
                .withdrawRequests(withdrawRequests)
                .sellerApplications(sellerApplications)
                .build();
    }

    public List<DashboardDTOs.ActivityResponse> getRecentActivity(UUID adminId) {
        String sql = """
                (SELECT 'USER_REGISTERED' as type, 'User ' || name || ' registered' as message, created_at as created_at FROM users ORDER BY created_at DESC LIMIT 5)
                UNION ALL
                (SELECT 'SHEET_UPLOADED' as type, 'New sheet: ' || title as message, created_at as created_at FROM sheets ORDER BY created_at DESC LIMIT 5)
                ORDER BY created_at DESC
                LIMIT 10;
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Timestamp ts = rs.getTimestamp("created_at");
                    return DashboardDTOs.ActivityResponse.builder()
                            .type(rs.getString("type"))
                            .message(rs.getString("message"))
                            .createdAt(ts != null ? ts.toLocalDateTime() : null)
                            .build();
                }
        );
    }

    public DashboardDTOs.SystemHealthResponse getSystemHealth(UUID adminId) {
        Long activeUsers = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE updated_at >= CURRENT_DATE - INTERVAL '1 days'",
                Long.class
        );

        return DashboardDTOs.SystemHealthResponse.builder()
                .database("OK")
                .storageUsage("Loading...")
                .activeUsers(activeUsers)
                .build();
    }

    private int parseRange(String range) {
        if (range == null || range.isEmpty()) {
            return 30;
        }
        try {
            return Integer.parseInt(range.replace("d", ""));
        } catch (NumberFormatException e) {
            return 30;
        }
    }
}