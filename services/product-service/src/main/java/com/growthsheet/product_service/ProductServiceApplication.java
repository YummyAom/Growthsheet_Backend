package com.growthsheet.product_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.growthsheet.product_service.config.client")
public class ProductServiceApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
        .directory("../..")
        .ignoreIfMissing()
        .load();
		dotenv.entries().forEach(
			e -> System.setProperty(e.getKey(), e.getValue())
		);
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
