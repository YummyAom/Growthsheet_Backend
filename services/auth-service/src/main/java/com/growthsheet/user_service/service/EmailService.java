package com.growthsheet.user_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setFrom(fromEmail);  // ดึงจาก env อัตโนมัติ
            helper.setTo(email);
            helper.setSubject("Your Verification Code - GrowthSheet");
            helper.setText(
                "<h2>รหัส OTP ของคุณคือ</h2>" +
                "<h1 style='letter-spacing:8px'>" + otp + "</h1>" +
                "<p>รหัสนี้จะหมดอายุใน <b>5 นาที</b></p>",
                true
            );

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}