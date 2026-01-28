package com.growthsheet.user_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${resend.apikey}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void sendEmail(String email, String otp) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("GrowthSheet <onboarding@resend.dev>")
                .to(email)
                .subject("Your Verification Code")
                .html("<h1>Your OTP is: " + otp + "</h1>")
                .build();

        try {
            resend.emails().send(params);
            System.out.println(" Email sent to " + email);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
