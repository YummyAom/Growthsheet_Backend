package com.growthsheet.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
        .directory("../../")
        .ignoreIfMissing()
        .load();
		dotenv.entries().forEach(
			e -> System.setProperty(e.getKey(), e.getValue())
		);
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
