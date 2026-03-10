package com.growthsheet.payment_service.service;

import com.growthsheet.payment_service.dto.SellerBalanceResponse;
import com.growthsheet.payment_service.dto.dashboard.DailySalesProjection;
import com.growthsheet.payment_service.dto.dashboard.MonthlySalesProjection;
import com.growthsheet.payment_service.dto.dashboard.SellerDashboardDTOs.MonthlySalesItem;
import com.growthsheet.payment_service.dto.dashboard.SellerDashboardDTOs.SellerDashboardSummaryResponse;
import com.growthsheet.payment_service.dto.dashboard.SellerDashboardDTOs.WeeklySalesItem;
import com.growthsheet.payment_service.dto.dashboard.SheetPerformanceProjection;
import com.growthsheet.payment_service.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerDashboardService {

    private final OrderItemRepository orderItemRepository;
    private final WithdrawalService withdrawalService;

    public Long getTotalSalesVolume(UUID sellerId) {
        return orderItemRepository.calculateTotalSalesVolumeBySellerId(sellerId);
    }

    public SellerDashboardSummaryResponse getSellerDashboardSummary(UUID sellerId) {

        // --- เดิม ---
        Long totalSalesVolume = orderItemRepository.calculateTotalSalesVolumeBySellerId(sellerId);
        List<SheetPerformanceProjection> sheetPerformances = orderItemRepository.getSheetPerformanceBySellerId(sellerId);
        SellerBalanceResponse balance = withdrawalService.getSellerBalance(sellerId);

        // --- ยอดวันนี้ ---
        BigDecimal todaySales = orderItemRepository.calculateTodaySalesBySellerId(sellerId);

        // --- Weekly Sales (7 วันย้อนหลัง) ---
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();
        List<DailySalesProjection> dailyRaw = orderItemRepository.getWeeklySalesBySellerId(sellerId, sevenDaysAgo);
        List<WeeklySalesItem> weeklySales = buildWeeklySales(dailyRaw);

        // --- Monthly Sales (6 เดือนย้อนหลัง) ---
        LocalDateTime sixMonthsAgo = LocalDate.now().minusMonths(5).withDayOfMonth(1).atStartOfDay();
        List<MonthlySalesProjection> monthlyRaw = orderItemRepository.getMonthlySalesBySellerId(sellerId, sixMonthsAgo);
        List<MonthlySalesItem> monthlySales = buildMonthlySales(monthlyRaw);

        // --- Top Sheet ---
        String topSheetTitle = null;
        BigDecimal topSheetRevenue = null;
        if (!sheetPerformances.isEmpty()) {
            SheetPerformanceProjection top = sheetPerformances.get(0);
            topSheetTitle = top.getSheetName();
            topSheetRevenue = top.getTotalRevenue();
        }

        return SellerDashboardSummaryResponse.builder()
                // เดิม
                .totalSalesVolume(totalSalesVolume)
                .totalRevenue(balance.getNetRevenue())
                .withdrawableAmount(balance.getAvailable())
                .sheetPerformances(sheetPerformances)
                // ใหม่
                .todaySales(todaySales)
                .totalBalance(balance.getAvailable())
                .totalOrders(totalSalesVolume)
                .weeklySales(weeklySales)
                .monthlySales(monthlySales)
                .topSheetTitle(topSheetTitle)
                .topSheetRevenue(topSheetRevenue)
                .build();
    }

    private List<WeeklySalesItem> buildWeeklySales(List<DailySalesProjection> raw) {
        Map<LocalDate, BigDecimal> map = raw.stream()
                .collect(Collectors.toMap(
                        DailySalesProjection::getSaleDate,
                        DailySalesProjection::getTotalAmount
                ));

        List<WeeklySalesItem> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String label = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            BigDecimal amount = map.getOrDefault(date, BigDecimal.ZERO);
            result.add(WeeklySalesItem.builder()
                    .day(label)
                    .amount(amount)
                    .build());
        }
        return result;
    }

    private List<MonthlySalesItem> buildMonthlySales(List<MonthlySalesProjection> raw) {
        Map<String, BigDecimal> map = raw.stream()
                .collect(Collectors.toMap(
                        p -> p.getYear() + "-" + p.getMonth(),
                        MonthlySalesProjection::getTotalAmount
                ));

        List<MonthlySalesItem> result = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusMonths(i);
            String key = date.getYear() + "-" + date.getMonthValue();
            String label = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            BigDecimal amount = map.getOrDefault(key, BigDecimal.ZERO);
            result.add(MonthlySalesItem.builder()
                    .month(label)
                    .amount(amount)
                    .build());
        }
        return result;
    }
}