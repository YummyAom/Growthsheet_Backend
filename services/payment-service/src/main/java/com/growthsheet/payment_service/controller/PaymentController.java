package com.growthsheet.payment_service.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.payment_service.config.client.OrderClient;
import com.growthsheet.payment_service.dto.OmiseWebhook;
import com.growthsheet.payment_service.dto.OrderResponse;
import com.growthsheet.payment_service.dto.OrderWithPaymentResponse;
import com.growthsheet.payment_service.dto.PromptPayResponse;
import com.growthsheet.payment_service.entity.Payment;
import com.growthsheet.payment_service.repository.PaymentRepository;
import com.growthsheet.payment_service.service.PaymentService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderClient orderClient;
    // private final ObjectMapper objectMapper;
    private final PaymentRepository paymentRepo;

    @GetMapping("/")
    public String getHello() {
        return "hello payment";
    }

    @PostMapping("/create-charge")
    public ResponseEntity<?> createCharge(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestParam UUID orderId) {

        try {

            PromptPayResponse response = paymentService.createNewPromptPayCharge(orderId, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "charge_id", response.chargeId(),
                    "qr_url", response.qrCodeUrl(),
                    "expires_at", response.expiresAt(),
                    "amount", response.amount()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderFromOrderService(
            @RequestHeader("X-USER-ID") UUID userId,
            @PathVariable UUID orderId) {

        try {
            OrderResponse order = orderClient.getOrderById(userId, orderId);
            Payment payment = paymentRepo.findByOrderId(orderId)
                    .orElse(null);
            OrderWithPaymentResponse response = new OrderWithPaymentResponse(order, payment);
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    @GetMapping("/orders/pending")
    public ResponseEntity<?> getPendingOrdersFromOrderService(
            @RequestHeader("X-USER-ID") UUID userId) {

        try {
            var orders = orderClient.getPendingOrders(userId);

            return ResponseEntity.ok(orders);

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestBody OmiseWebhook webhook) {

        try {

            if (!"charge.complete".equals(webhook.key())) {
                return ResponseEntity.ok().build();
            }

            String chargeId = webhook.data().id();

            if (chargeId != null && !chargeId.isBlank()) {
                paymentService.processSuccessfulCharge(chargeId);
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
