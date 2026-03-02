package com.growthsheet.payment_service.config;

import feign.Client;
import feign.okhttp.OkHttpClient;
import okhttp3.ConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignOkHttpConfig {

    @Bean
    public Client feignClient() {
        okhttp3.OkHttpClient okHttp = new okhttp3.OkHttpClient.Builder()
                .connectionPool(new ConnectionPool())
                .build();

        return new OkHttpClient(okHttp);
    }
}