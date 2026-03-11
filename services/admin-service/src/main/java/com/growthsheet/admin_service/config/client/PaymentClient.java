package com.growthsheet.admin_service.config.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-service",
        url = "${GATEWAY_SERVICE_URL}"
)
public interface PaymentClient {
    @GetMapping("/payments/refunds/admin/pending")
    ResponseEntity<List<Map<String, Object>>> getPendingRefunds(@RequestHeader("X-USER-ID") UUID adminId);

    @PatchMapping("/payments/refunds/admin/{refundId}/approve")
    ResponseEntity<Map<String, Object>> approveRefund(
            @RequestHeader("X-USER-ID") UUID adminId,
            @PathVariable("refundId") UUID refundId,
            @RequestBody Map<String, Object> req);

    @PatchMapping("/payments/refunds/admin/{refundId}/reject")
    ResponseEntity<Map<String, Object>> rejectRefund(
            @RequestHeader("X-USER-ID") UUID adminId,
            @PathVariable("refundId") UUID refundId,
            @RequestBody Map<String, Object> req);
}
