package com.growthsheet.payment_service.service;

// import co.omise.Client;
// import co.omise.models.Charge;
// import co.omise.models.Source;
// import co.omise.requests.Request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.SessionCheckMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.growthsheet.payment_service.config.client.OrderClient;
import com.growthsheet.payment_service.dto.PaymentStatus;
import com.growthsheet.payment_service.entity.Payment;
import com.growthsheet.payment_service.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    @Value("${growthsheet.frontend.url}")
    private String frontendUrl; 

    public PaymentService(
            OrderClient orderClient,
            PaymentRepository paymentRepository,
            @Value("${stripe.public_key}") String publicKey,
            @Value("${stripe.secret_key}") String secretKey) throws Exception {

        this.paymentRepository = paymentRepository;
        this.orderClient = orderClient;
        Stripe.apiKey = secretKey;

    }

    @Transactional
    public String createStripeSession(UUID orderId, UUID userId) throws Exception {
        var order = orderClient.getOrderById(userId, orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        long amountInCents = order.getTotalPrice().multiply(new BigDecimal(100)).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/payment/cancel")
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

        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .chargeId(session.getId())
                .amount(amountInCents)
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        return session.getUrl();
    }

    // @Transactional
    // public PromptPayResponse createNewPromptPayCharge(UUID orderId, UUID userId)
    // throws Exception {
    // // ตรวจสอบว่า order มีอยู่จริงผ่าน Feign Client
    // // ถ้าตรงนี้พ่น 404 ให้เช็คว่าฝั่ง Order Service เปิด Path /order/{id}
    // ไว้จริงไหม
    // var order = orderClient.getOrderById(userId, orderId);
    // if (order == null) {
    // throw new RuntimeException("Order not found from Order Service");
    // }

    // BigDecimal amountBaht = order.getTotalPrice();
    // long amountInSatang =
    // amountBaht.multiply(BigDecimal.valueOf(100)).longValue();

    // // ✅ STEP 1: สร้าง PromptPay Source
    // Request<Source> sourceRequest =
    // new Source.CreateRequestBuilder()
    // .amount(amountInSatang)
    // .currency("thb")
    // .type("promptpay")
    // .build();

    // Source source = omiseClient.sendRequest(sourceRequest);

    // // ✅ STEP 2: เอา source.id ไปสร้าง Charge
    // Request<Charge> chargeRequest =
    // new Charge.CreateRequestBuilder()
    // .amount(amountInSatang)
    // .currency("thb")
    // .source(source.getId())
    // .build();

    // Charge charge = omiseClient.sendRequest(chargeRequest);

    // // ✅ STEP 3: Save DB
    // Payment payment = Payment.builder()
    // .id(UUID.randomUUID())
    // .orderId(orderId)
    // .chargeId(charge.getId())
    // .amount(amountInSatang)
    // .status(PaymentStatus.PENDING)
    // .build();

    // paymentRepository.save(payment);

    // // ✅ STEP 4: ดึง QR Code URL
    // // ระวัง: ต้องเช็ค ScannableCode ว่ามีค่าไหมป้องกัน Null
    // String qrCodeUrl = "";
    // if (source.getScannableCode() != null && source.getScannableCode().getImage()
    // != null) {
    // qrCodeUrl = source.getScannableCode().getImage().getDownloadUri();
    // }

    // // แก้ไขจุดที่พิมพ์ผิด: .getExpiresAt ต้องมี ()
    // LocalDateTime expiresAt = LocalDateTime.ofInstant(
    // java.time.Instant.ofEpochMilli(source.getExpiresAt().getMillis()),
    // ZoneId.systemDefault());

    // return new PromptPayResponse(
    // charge.getId(),
    // qrCodeUrl,
    // expiresAt,
    // amountBaht);
    // }

    // @Transactional
    // public PromptPayResponse getOrRefreshPromptPay(UUID orderId, UUID userId)
    // throws Exception {
    // Optional<Payment> existing =
    // paymentRepository.findByOrderIdAndStatus(orderId, PaymentStatus.PENDING);

    // if (existing.isPresent()) {
    // Charge charge = omiseClient.sendRequest(
    // new Charge.GetRequestBuilder(existing.get().getChargeId()).build());

    // // ตรวจสอบสถานะถ้ายังไม่จ่ายและไม่หมดอายุให้คืนค่าเดิม
    // if (!charge.isExpired() && !charge.isPaid()) {
    // Source source = charge.getSource();

    // String qrCodeUrl = (source.getScannableCode() != null)
    // ? source.getScannableCode().getImage().getDownloadUri()
    // : "";

    // LocalDateTime expiresAt = LocalDateTime.ofInstant(
    // java.time.Instant.ofEpochMilli(source.getAmount().getMillis()),
    // ZoneId.systemDefault());

    // BigDecimal amountBaht = BigDecimal.valueOf(charge.getAmount())
    // .divide(BigDecimal.valueOf(100));

    // return new PromptPayResponse(
    // charge.getId(),
    // qrCodeUrl,
    // expiresAt,
    // amountBaht);
    // }

    // // ถ้าสถานะเปลี่ยนไปแล้วให้ Update DB
    // if (charge.isPaid()) {
    // existing.get().setStatus(PaymentStatus.PAID);
    // paymentRepository.save(existing.get());
    // } else if (charge.isExpired()) {
    // existing.get().setStatus(PaymentStatus.PENDING);
    // paymentRepository.save(existing.get());
    // }
    // }

    // // ถ้าไม่มีของเดิม หรือของเดิมใช้ไม่ได้แล้ว ให้สร้างใหม่
    // return createNewPromptPayCharge(orderId, userId);
    // }

    // @Transactional
    // public void processSuccessfulCharge(String chargeId) throws Exception {
    // Charge charge = omiseClient.sendRequest(
    // new Charge.GetRequestBuilder(chargeId).build());

    // if (!charge.isPaid()) {
    // return;
    // }

    // Payment payment = paymentRepository.findByChargeId(chargeId)
    // .orElseThrow(() -> new RuntimeException("Payment record not found in database
    // for charge: " + chargeId));

    // if (payment.getStatus() == PaymentStatus.PAID) {
    // return;
    // }

    // payment.setStatus(PaymentStatus.PAID);
    // paymentRepository.save(payment);

    // // แจ้งฝั่ง Order ว่าจ่ายเงินแล้ว
    // orderClient.markOrderAsPaid(payment.getOrderId());
    // }
}