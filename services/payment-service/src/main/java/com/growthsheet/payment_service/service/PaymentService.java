package com.growthsheet.payment_service.service;

import co.omise.Client;
import co.omise.models.Charge;
import co.omise.requests.Request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.growthsheet.payment_service.config.client.OrderClient;
import com.growthsheet.payment_service.dto.PaymentStatus;
import com.growthsheet.payment_service.dto.PromptPayResponse;
import com.growthsheet.payment_service.entity.Payment;
import com.growthsheet.payment_service.repository.PaymentRepository;

@Service
public class PaymentService {
        private final Client omiseClient;
        private final PaymentRepository paymentRepository;
        private final OrderClient orderClient;

        public PaymentService(
                        OrderClient orderClient,
                        PaymentRepository paymentRepository,
                        @Value("${omise.public_key}") String publicKey,
                        @Value("${omise.secret_key}") String secretKey) throws Exception {

                this.paymentRepository = paymentRepository;
                this.orderClient = orderClient;
                this.omiseClient = new Client.Builder()
                                .publicKey(publicKey)
                                .secretKey(secretKey)
                                .build();
        }

        @Transactional
        public PromptPayResponse createNewPromptPayCharge(UUID orderId, UUID userId) throws Exception {
                // 1. ดึง order เพื่อเอาราคาที่ถูกต้อง
                var order = orderClient.getOrderById(userId, orderId);

                BigDecimal amountBaht = order.getTotalPrice();
                long amountInSatang = amountBaht
                                .multiply(BigDecimal.valueOf(100))
                                .longValue();

                // 2. สร้าง Charge
                Request<Charge> chargeRequest = new Charge.CreateRequestBuilder()
                                .amount(amountInSatang)
                                .currency("thb")
                                .source("promptpay")
                                .build();

                Charge charge = omiseClient.sendRequest(chargeRequest);

                // 3. บันทึกข้อมูลการชำระเงินลง Database
                Payment payment = Payment.builder()
                                .id(UUID.randomUUID())
                                .orderId(orderId)
                                .chargeId(charge.getId())
                                .amount(amountInSatang)
                                .status(PaymentStatus.PENDING)
                                .build();

                paymentRepository.save(payment);

                // 4. ดึง URL ของ QR Code
                String qrCodeUrl = charge.getSource()
                                .getScannableCode()
                                .getImage()
                                .getDownloadUri();

                // 5. แก้ไขการแปลงเวลา (Joda-Time -> Java Time)
                LocalDateTime expiresAt = LocalDateTime.ofInstant(
                                java.time.Instant.ofEpochMilli(charge.getExpiresAt().getMillis()),
                                ZoneId.systemDefault());

                return new PromptPayResponse(
                                charge.getId(),
                                qrCodeUrl,
                                expiresAt,
                                amountBaht);
        }

        @Transactional
        public PromptPayResponse getOrRefreshPromptPay(UUID orderId, UUID userId) throws Exception {

                // 1. หา Payment ที่ยัง PENDING อยู่
                Optional<Payment> existing = paymentRepository
                                .findByOrderIdAndStatus(orderId, PaymentStatus.PENDING);

                if (existing.isPresent()) {

                        // 2. ดึงข้อมูล Charge ล่าสุดจาก Omise
                        Charge charge = omiseClient.sendRequest(
                                        new Charge.GetRequestBuilder(existing.get().getChargeId()).build());

                        // 3. ถ้ายังไม่หมดอายุ และยังไม่จ่าย
                        if (!charge.isExpired() && !charge.isPaid()) {

                                String qrCodeUrl = charge.getSource()
                                                .getScannableCode()
                                                .getImage()
                                                .getDownloadUri();

                                LocalDateTime expiresAt = LocalDateTime.ofInstant(
                                                java.time.Instant.ofEpochMilli(charge.getExpiresAt().getMillis()),
                                                ZoneId.systemDefault());

                                BigDecimal amountBaht = BigDecimal
                                                .valueOf(charge.getAmount())
                                                .divide(BigDecimal.valueOf(100));

                                return new PromptPayResponse(
                                                charge.getId(),
                                                qrCodeUrl,
                                                expiresAt,
                                                amountBaht);
                        }

                        // ถ้า expired หรือ paid แล้ว → เปลี่ยนสถานะใน DB
                        if (charge.isPaid()) {
                                existing.get().setStatus(PaymentStatus.PAID);
                                paymentRepository.save(existing.get());
                        } else if (charge.isExpired()) {
                                existing.get().setStatus(PaymentStatus.PAID);
                                paymentRepository.save(existing.get());
                        }
                }

                // 4. ถ้าไม่มี PENDING หรือหมดอายุ → สร้างใหม่
                return createNewPromptPayCharge(orderId, userId);
        }

        @Transactional
        public void processSuccessfulCharge(String chargeId) throws Exception {

                Charge charge = omiseClient.sendRequest(
                                new Charge.GetRequestBuilder(chargeId).build());

                if (!charge.isPaid()) {
                        return;
                }

                Payment payment = paymentRepository.findByChargeId(chargeId)
                                .orElseThrow(() -> new RuntimeException("Payment not found"));

                if (payment.getStatus() == PaymentStatus.PAID) {
                        return;
                }

                payment.setStatus(PaymentStatus.PAID);
                paymentRepository.save(payment);

                orderClient.markOrderAsPaid(payment.getOrderId());
        }
}
