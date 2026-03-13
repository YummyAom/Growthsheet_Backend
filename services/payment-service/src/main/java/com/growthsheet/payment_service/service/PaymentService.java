package com.growthsheet.payment_service.service;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.growthsheet.payment_service.config.client.NotificationClient;
import com.growthsheet.payment_service.config.client.OrderClient;
import com.growthsheet.payment_service.dto.NotificationRequest;
import com.growthsheet.payment_service.dto.PaymentStatus;
import com.growthsheet.payment_service.dto.SellerSummary;
import com.growthsheet.payment_service.entity.OrderItem;
import com.growthsheet.payment_service.entity.Payment;
import com.growthsheet.payment_service.repository.OrderItemRepository;
import com.growthsheet.payment_service.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final NotificationClient notificationClient;
    private final OrderItemRepository orderItemRepository;
    @Value("${stripe.mobile.success-url}")
    private String successUrl;

    @Value("${stripe.mobile.cancel-url}")
    private String cancelUrl;

    public PaymentService(
            OrderClient orderClient,
            PaymentRepository paymentRepository,
            NotificationClient notificationClient,
            OrderItemRepository orderItemRepository,
            @Value("${stripe.public_key}") String publicKey,
            @Value("${stripe.secret_key}") String secretKey) {

        this.paymentRepository = paymentRepository;
        this.orderClient = orderClient;
        this.notificationClient = notificationClient;
        this.orderItemRepository = orderItemRepository;
        Stripe.apiKey = secretKey;
    }

    @Transactional
    public String createStripeSession(UUID orderId, UUID userId) throws Exception {

        var order = orderClient.getOrderById(userId, orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        long amountInCents = order.getTotalPrice()
                .multiply(new BigDecimal(100))
                .longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
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
                    .userId(userId)
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

        if (session.getMetadata() == null)
            return;

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

        // ✅ Notify Order Service
        try {
            orderClient.markOrderAsPaid(orderId);
        } catch (Exception e) {
            log.error("Failed to notify OrderService for order: " + orderId, e);
        }

        try {
            NotificationRequest req = new NotificationRequest();
            req.setUserId(payment.getUserId());
            req.setTitle("ชำระเงินสำเร็จ 🎉");
            req.setMessage("คำสั่งซื้อ " + orderId + " ชำระเงินเรียบร้อยแล้ว");

            notificationClient.createNotification(req);

        } catch (Exception e) {
            log.error("Notification failed", e);
        }
        // ===== แจ้งผู้ขาย =====
        try {

            List<SellerSummary> items = orderItemRepository.findSellerSummaryByOrderId(orderId);
            for (SellerSummary item : items) {
                // System.out.println("=== seller_id: " + item.getSeller_id());
                // System.out.println("=== seller_name: " + item.getSeller_name());
                // System.out.println("=== total: " + item.getTotal());

                NotificationRequest req = new NotificationRequest();
                req.setUserId(item.getSeller_id());
                req.setTitle("มีคนซื้อชีทของคุณ 🎉");
                req.setMessage("ชีท \"" + item.getSheet_names() + "\" ถูกซื้อแล้ว ราคา " + item.getTotal() + " บาท");

                notificationClient.createNotification(req);
            }

        } catch (Exception e) {
            log.error("Seller notification failed", e);
        }
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