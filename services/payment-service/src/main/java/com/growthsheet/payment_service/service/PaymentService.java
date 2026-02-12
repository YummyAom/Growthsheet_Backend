package com.growthsheet.payment_service.service;

import co.omise.Client;
import co.omise.models.Charge;
import co.omise.requests.Request;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;

import com.growthsheet.payment_service.dto.PaymentRequest;

public class PaymentService {
    private final Client omiseClient;

    public PaymentService(
            @Value("${omise.public_key}") String publicKey,
            @Value("${omise.secret_key}") String secretKey) throws Exception {
        this.omiseClient = new Client.Builder()
                .publicKey(publicKey)
                .secretKey(secretKey)
                .build();
    }

    public Charge createPromptPayCharge(PaymentRequest request) throws Exception {
        long amountInSatang = request.amount().multiply(new BigDecimal(100)).longValue();
        Request<Charge> chargeRequest = new Charge.CreateRequestBuilder()
                .amount(amountInSatang)
                .currency("thb")
                .source(request.source())
                .build();
        return omiseClient.sendRequest(chargeRequest);
    }
}
