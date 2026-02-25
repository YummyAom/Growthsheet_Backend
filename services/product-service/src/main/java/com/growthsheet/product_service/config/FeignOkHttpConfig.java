package com.growthsheet.product_service.config;

import feign.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.okhttp.OkHttpClient;



@Configuration
public class FeignOkHttpConfig {

    @Bean
    public Client feignClient() {
        return new OkHttpClient();
    }
}