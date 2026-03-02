package com.growthsheet.apigateway_service.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public AuthenticationFilter(ReactiveRedisTemplate<String, Object> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getPath().toString();
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            // 1️⃣ เช็ค Authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            String accessToken = authHeader.substring(7);
            String redisKey = "access_token:" + accessToken;

            // 2️⃣ เช็ค session ใน Redis
            return redisTemplate.opsForHash().entries(redisKey)
                    .collectMap(
                            entry -> entry.getKey().toString(),
                            Map.Entry::getValue
                    )
                    .flatMap(sessionData -> {

                        if (sessionData.isEmpty()) {
                            return onError(exchange, "Session not found", HttpStatus.UNAUTHORIZED);
                        }

                        Object userIdObj = sessionData.get("user_id");
                        Object roleObj = sessionData.get("role");

                        if (userIdObj == null || roleObj == null) {
                            return onError(exchange, "Invalid session data", HttpStatus.UNAUTHORIZED);
                        }

                        String userId = userIdObj.toString();
                        String role = roleObj.toString();

                        // 3️⃣ 🔐 เช็คเฉพาะ /admin/**
                        if (path.startsWith("/admin/") || path.equals("/admin")) {
                            if (!"ADMIN".equals(role)) {
                                return onError(exchange, "Admin access only", HttpStatus.FORBIDDEN);
                            }
                        }

                        // 4️⃣ ส่ง header ต่อไปยัง downstream service
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-USER-ID", userId)
                                .header("X-USER-ROLE", role)
                                .build();

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    })
                    .switchIfEmpty(onError(exchange, "Session not found", HttpStatus.UNAUTHORIZED))
                    .onErrorResume(e -> onError(exchange, "Unauthorized", HttpStatus.UNAUTHORIZED));
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        String body = String.format("{\"error\":\"%s\",\"status\":%d}", message, status.value());
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return response.writeWith(Mono.fromSupplier(() ->
                response.bufferFactory().wrap(bytes)
        ));
    }
}