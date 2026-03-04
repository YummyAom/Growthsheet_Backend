package com.growthsheet.payment_service.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Value("${SERVICE_TOKEN}")
    private String serviceToken;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String authHeader = request.getHeader("Authorization");

                // ถ้ามี user → ใช้ user
                if (authHeader != null) {
                    System.out.println("Feign using USER Authorization: " + authHeader);
                    template.header("Authorization", authHeader);
                    return;
                }
            }

            // ไม่มี user → ใช้ internal
            System.out.println("Feign using INTERNAL token");
            template.header("X-INTERNAL-TOKEN", serviceToken);
        };
    }
}