package com.growthsheet.admin_service.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignMultipartConfig {

    @Bean
    public Encoder feignFormEncoder() {
        // ใช้ SpringFormEncoder ตรงๆ โดยไม่ต้องพึ่ง HttpMessageConverters
        return new SpringFormEncoder();
    }
}