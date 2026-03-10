package com.growthsheet.payment_service.service;

import com.growthsheet.payment_service.dto.SellerBalanceResponse;
import com.growthsheet.payment_service.dto.dashboard.SellerDashboardDTOs.SellerDashboardSummaryResponse;
import com.growthsheet.payment_service.dto.dashboard.SheetPerformanceProjection;
import com.growthsheet.payment_service.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerDashboardService {

    private final OrderItemRepository orderItemRepository;
    private final WithdrawalService withdrawalService;

    public Long getTotalSalesVolume(UUID sellerId) {
        return orderItemRepository.calculateTotalSalesVolumeBySellerId(sellerId);
    }

    /**
     * ดึงข้อมูล dashboard สำหรับ seller
     * รวมยอดขาย, รายได้, รูปแบบชีทที่ขายได้ และยอดที่ถอนได้
     */
    public SellerDashboardSummaryResponse getSellerDashboardSummary(UUID sellerId) {
        Long totalSalesVolume = orderItemRepository.calculateTotalSalesVolumeBySellerId(sellerId);
        List<SheetPerformanceProjection> sheetPerformances = orderItemRepository.getSheetPerformanceBySellerId(sellerId);
        SellerBalanceResponse balance = withdrawalService.getSellerBalance(sellerId);

        return SellerDashboardSummaryResponse.builder()
                .totalSalesVolume(totalSalesVolume)
                .totalRevenue(balance.getNetRevenue())
                .withdrawableAmount(balance.getAvailable())
                .sheetPerformances(sheetPerformances)
                .build();
    }
}
