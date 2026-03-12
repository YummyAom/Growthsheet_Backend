package com.growthsheet.product_service.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.growthsheet.product_service.config.client.OrderClient;
import com.growthsheet.product_service.dto.response.*;
import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.repository.SheetRepository;
import com.growthsheet.product_service.repository.UserRepository;

@Service
public class SellerAnalyticsService {

        private final SheetRepository sheetRepo;
        private final OrderClient orderClient;
        private final UserRepository userRepo;

        public SellerAnalyticsService(
                        SheetRepository sheetRepo,
                        OrderClient orderClient,
                        UserRepository userRepo) {

                this.sheetRepo = sheetRepo;
                this.orderClient = orderClient;
                this.userRepo = userRepo;
        }

        // 👈 เพิ่มรับค่า String period
        public SellerAnalyticsResponse getSellerAnalytics(UUID sellerId, String period) {

                // 1️⃣ ดึง sheet ของ seller
                List<Sheet> sheets = sheetRepo.findAllBySellerId(sellerId);

                List<UUID> sheetIds = sheets.stream()
                                .map(Sheet::getId)
                                .toList();

                // 2️⃣ ดึงยอดขายจาก order service
                Map<UUID, Long> salesMapTemp = new HashMap<>();

                if (!sheetIds.isEmpty()) {
                        try {
                                salesMapTemp = orderClient.getSalesCountsBySheetIds(sheetIds);
                        } catch (Exception e) {
                                System.err.println("Cannot fetch sales counts: " + e.getMessage());
                        }
                }

                final Map<UUID, Long> salesMap = salesMapTemp;

                // ⭐ เพิ่มตรงนี้: ดึงยอดขายรายวันแบบระบุช่วงเวลา (7 วัน หรือ 1 เดือน) ⭐
                List<DailySaleDTO> dailySales = Collections.emptyList();
                if (!sheetIds.isEmpty()) {
                        try {
                                // ส่ง period ไปให้ฝั่ง Order Service จัดการ
                                dailySales = orderClient.getDailySalesBySheetIds(sheetIds, period);
                        } catch (Exception e) {
                                System.err.println("Cannot fetch daily sales: " + e.getMessage());
                        }
                }

                // 3️⃣ คำนวณยอดขายรวม
                long totalSales = salesMap.values().stream()
                                .mapToLong(Long::longValue)
                                .sum();

                // 4️⃣ Top 3 Sheets
                List<SheetPerformanceDTO> topSheets = sheets.stream()
                                .map(sheet -> new SheetPerformanceDTO(
                                                sheet.getId(),
                                                sheet.getTitle(),
                                                salesMap.getOrDefault(sheet.getId(), 0L)))
                                .sorted((a, b) -> Long.compare(b.salesVolume(), a.salesVolume()))
                                .limit(3)
                                .toList();

                // 5️⃣ Faculty distribution
                List<UUID> buyerIds = Collections.emptyList();
                if (!sheetIds.isEmpty()) {
                        try {
                                buyerIds = orderClient.getBuyerIdsBySheetIds(sheetIds);
                        } catch (Exception e) {
                                System.err.println("Cannot fetch buyer ids: " + e.getMessage());
                        }
                }

                List<FacultyDistributionDTO> facultyDistribution = userRepo.findAllById(buyerIds).stream()
                                .map(user -> {
                                        String faculty = user.getFaculty();
                                        if (faculty == null || faculty.isBlank()) {
                                                faculty = "อื่นๆ";
                                        }
                                        return faculty;
                                })
                                .collect(
                                                java.util.stream.Collectors.groupingBy(
                                                                f -> f,
                                                                java.util.stream.Collectors.counting()))
                                .entrySet()
                                .stream()
                                .map(entry -> new FacultyDistributionDTO(
                                                entry.getKey(),
                                                entry.getValue()))
                                .toList();

                // ✅ เพิ่ม dailySales ตรงนี้
                return new SellerAnalyticsResponse(
                                totalSales,
                                dailySales,
                                topSheets,
                                facultyDistribution);
        }
}