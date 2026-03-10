package com.growthsheet.payment_service.controller;

import com.growthsheet.payment_service.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments/internal")
@RequiredArgsConstructor
public class InternalPaymentController {

    private final OrderItemRepository orderItemRepository;

    @PostMapping("/sales-counts")
    public Map<UUID, Long> getSalesCounts(@RequestBody List<UUID> sheetIds) {
        if (sheetIds == null || sheetIds.isEmpty()) {
            return Map.of();
        }

        // สมมติว่าใน OrderItem มีฟิลด์ sheetId 
        // คุณต้องสร้าง Query ใน OrderItemRepository เพื่อ Group By และ Count นะครับ
        // เช่น: return orderItemRepository.countSalesBySheetIds(sheetIds);
        
        // ตัวอย่างการ Mock คร่าวๆ ให้ดูก่อน (คุณต้องไปเขียน Query จริงใน Repository)
        List<Object[]> results = orderItemRepository.countSalesBySheetIds(sheetIds);
        
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],  // sheetId
                        row -> (Long) row[1]   // count
                ));
    }
}