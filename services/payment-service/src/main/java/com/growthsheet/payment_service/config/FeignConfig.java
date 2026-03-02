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
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            String authHeader = null;

            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                authHeader = request.getHeader("Authorization");
            }

            if (authHeader != null) {
                template.header("Authorization", authHeader);
            }
            else {
                template.header("Authorization", "Bearer " + serviceToken);
            }
        };
    }
}