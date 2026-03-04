package com.growthsheet.payment_service.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.growthsheet.payment_service.config.client.OrderClient;
import com.growthsheet.payment_service.dto.OrderResponse;
import com.growthsheet.payment_service.dto.OrderWithPaymentResponse;
import com.growthsheet.payment_service.entity.Payment;
import com.growthsheet.payment_service.repository.PaymentRepository;
import com.growthsheet.payment_service.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderClient orderClient;
    
    private final PaymentRepository paymentRepo;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @GetMapping("/")
    public String getHello() {
        return "hello payment";
    }

    @PostMapping("/create-checkout-session/{orderId}")
    public ResponseEntity<?> createCheckoutSession(
            @RequestHeader("X-USER-ID") UUID userId,
            @PathVariable UUID orderId) {

        try {
            String checkoutUrl = paymentService.createStripeSession(orderId, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "checkout_url", checkoutUrl));

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
            Payment payment = paymentRepo.findByOrderId(orderId).orElse(null);
            return ResponseEntity.ok(new OrderWithPaymentResponse(order, payment));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/order/{orderId}/status")
    public ResponseEntity<?> getPaymentStatus(
            @PathVariable UUID orderId) {

        Payment payment = paymentRepo.findByOrderId(orderId)
                .orElse(null);

        if (payment == null) {
            return ResponseEntity.ok(Map.of("status", "NOT_FOUND"));
        }

        return ResponseEntity.ok(
                Map.of("status", payment.getStatus()));
    }

    @GetMapping("/orders/pending")
    public ResponseEntity<?> getPendingOrdersFromOrderService(
            @RequestHeader("X-USER-ID") UUID userId) {

        try {
            return ResponseEntity.ok(orderClient.getPendingOrders(userId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ===== Stripe Webhook =====
    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        System.out.print("Hello");
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid signature"));
        }

        switch (event.getType()) {

            case "checkout.session.completed":
                System.out.print("ok");
                paymentService.handleCheckoutCompleted(event);
                break;

            case "checkout.session.async_payment_succeeded":
                paymentService.handleCheckoutCompleted(event);
                break;

            case "checkout.session.async_payment_failed":
                paymentService.handleCheckoutExpired(event);
                break;

            case "checkout.session.expired":
                paymentService.handleCheckoutExpired(event);
                break;
        }
        return ResponseEntity.ok(Map.of("received", true));
    }
}