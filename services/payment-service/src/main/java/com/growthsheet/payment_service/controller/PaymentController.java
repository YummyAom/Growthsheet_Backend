package com.growthsheet.payment_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.payment_service.dto.PaymentRequest;
import com.growthsheet.payment_service.service.PaymentService;

import co.omise.models.Charge;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-charge")
    public ResponseEntity<?> createCharge(@RequestBody PaymentRequest request) {
        try {
            Charge charge = paymentService.createPromptPayCharge(request);

            if ("pending".equals(charge.getStatus().toString())) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "charge_id", charge.getId(),
                        "qr_url", charge.getSource().getScannableCode().getImage().getDownloadUri()));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Status: " + charge.getStatus()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
