package com.growthsheet.payment_service.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.growthsheet.payment_service.config.client.OrderClient;
import com.growthsheet.payment_service.dto.PaymentStatus;
import com.growthsheet.payment_service.entity.Payment;
import com.growthsheet.payment_service.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    @Value("${stripe.mobile.success-url}")
    private String successUrl;

    @Value("${stripe.mobile.cancel-url}")
    private String cancelUrl;

    public PaymentService(
            OrderClient orderClient,
            PaymentRepository paymentRepository,
            @Value("${stripe.public_key}") String publicKey,
            @Value("${stripe.secret_key}") String secretKey) {

        this.paymentRepository = paymentRepository;
        this.orderClient = orderClient;
        Stripe.apiKey = secretKey;
    }

    @Transactional
    public String createStripeSession(UUID orderId, UUID userId) throws Exception {

        var order = orderClient.getOrderById(userId, orderId);
        System.out.print(orderId);
        if (order == null) {    
            throw new RuntimeException("Order not found");
        }

        long amountInCents = order.getTotalPrice()
                .multiply(new BigDecimal(100))
                .longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setClientReferenceId(orderId.toString())
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.PROMPTPAY)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("thb")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("GrowthSheet Order: " + orderId)
                                                                .build())
                                                .build())
                                .build())
                .putMetadata("orderId", orderId.toString())
                .build();

        Session session = Session.create(params);

        Optional<Payment> existing = paymentRepository.findByOrderId(orderId);
        if (existing.isEmpty()) {
            Payment payment = Payment.builder()
                    .id(UUID.randomUUID())
                    .orderId(orderId)
                    .chargeId(session.getId())
                    .amount(amountInCents)
                    .status(PaymentStatus.PENDING)
                    .build();

            paymentRepository.save(payment);
        }

        return session.getUrl();
    }

    // ===== SUCCESS =====
    @Transactional
    public void handleCheckoutCompleted(Event event) {

        var stripeObject = event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (stripeObject == null)
            return;

        Session session = (Session) stripeObject;

        String orderIdStr = session.getMetadata().get("orderId");
        if (orderIdStr == null)
            return;

        UUID orderId = UUID.fromString(orderIdStr);

        Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);
        if (payment == null)
            return;

        if (payment.getStatus() == PaymentStatus.PAID)
            return;

        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        orderClient.markOrderAsPaid(orderId);
    }

    // ===== EXPIRED / FAILED =====
    @Transactional
    public void handleCheckoutExpired(Event event) {

        var stripeObject = event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (stripeObject == null)
            return;

        Session session = (Session) stripeObject;

        String orderIdStr = session.getMetadata().get("orderId");
        if (orderIdStr == null)
            return;

        UUID orderId = UUID.fromString(orderIdStr);

        Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);
        if (payment == null)
            return;

        if (payment.getStatus() == PaymentStatus.PAID)
            return;

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }
}