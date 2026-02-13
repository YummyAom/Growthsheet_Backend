package com.growthsheet.payment_service.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes attrs = 
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                
                // 1. ส่งต่อ Token สำหรับตรวจสอบสิทธิ์ (กัน 401)
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null) {
                    template.header("Authorization", authHeader);
                }

                // 2. ส่งต่อ X-USER-ID เพื่อระบุตัวตนผู้ใช้ (ตามที่ Controller ต้องการ)
                String userIdHeader = request.getHeader("X-USER-ID");
                if (userIdHeader != null) {
                    template.header("X-USER-ID", userIdHeader);
                }
            }
        };
    }
}