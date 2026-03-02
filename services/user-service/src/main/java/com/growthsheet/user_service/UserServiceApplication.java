package com.growthsheet.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class UserServiceApplication {

    @Value("${POSTGRES_URL:NOT_FOUND}")
    private String rawPostgresUrl;

    @Value("${spring.datasource.url:NOT_FOUND}")
    private String springDatasourceUrl;

    @Value("${REDIS_URL:NOT_FOUND}")
    private String redisUrl;

    @PostConstruct
    public void printDatabaseConfig() {
        System.out.println("=======================================");
        System.out.println("🔥 USER SERVICE ENV CHECK");
        System.out.println("POSTGRES_URL = " + rawPostgresUrl);
        System.out.println("spring.datasource.url = " + springDatasourceUrl);
        System.out.println("REDIS_URL = " + redisUrl);
        System.out.println("=======================================");
    }

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