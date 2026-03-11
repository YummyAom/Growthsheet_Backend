package com.growthsheet.admin_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.growthsheet.admin_service.config.client.FileClient;
import com.growthsheet.admin_service.config.client.PaymentClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefundAdminService {

    private final PaymentClient paymentClient;
    private final FileClient fileClient;

    public List<Map<String, Object>> getRefundsByStatus(String status, UUID adminId) {
        return paymentClient.getRefundsByStatus(adminId, status).getBody();
    }

    public Map<String, Object> approveRefund(UUID refundId, MultipartFile slipFile, String adminComment, UUID adminId) {
        // 1. Upload slip to Cloudinary via file-service
        Map<String, Object> uploadResult = fileClient.uploadSlip(slipFile);
        String slipUrl = (String) uploadResult.get("url");

        // 2. Call payment-service to approve
        Map<String, Object> req = Map.of(
                "refundSlipUrl", slipUrl,
                "adminComment", adminComment == null ? "" : adminComment);
        return paymentClient.approveRefund(adminId, refundId, req).getBody();
    }

    public Map<String, Object> rejectRefund(UUID refundId, String adminComment, UUID adminId) {
        if (adminComment == null || adminComment.isBlank()) {
            throw new RuntimeException("Admin comment is required for rejection");
        }

        Map<String, Object> req = Map.of(
                "adminComment", adminComment);
        return paymentClient.rejectRefund(adminId, refundId, req).getBody();
    }
}
