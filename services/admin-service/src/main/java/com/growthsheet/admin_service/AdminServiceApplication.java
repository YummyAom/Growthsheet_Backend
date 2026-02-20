package com.growthsheet.admin_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableFeignClients
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class AdminServiceApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory("../../")
				.ignoreIfMissing()
				.load();
		dotenv.entries().forEach(
				e -> System.setProperty(e.getKey(), e.getValue()));
		SpringApplication.run(AdminServiceApplication.class, args);
	}

}
