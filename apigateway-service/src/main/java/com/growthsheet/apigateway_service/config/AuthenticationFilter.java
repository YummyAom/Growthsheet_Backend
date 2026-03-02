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

    public static class Config { }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String path = exchange.getRequest().getPath().toString();

            // 1. ตรวจสอบ Header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String accessToken = authHeader.substring(7);
            String redisKey = "access_token:" + accessToken;

            // 2. ดึงข้อมูลจาก Redis แบบ Hash
            return redisTemplate.opsForHash().entries(redisKey)
                    .collectMap(ev -> ev.getKey().toString(), ev -> ev.getValue())
                    .flatMap(sessionData -> {
                        // เช็คว่าเจอ Session ไหม
                        if (sessionData == null || sessionData.isEmpty()) {
                            return Mono.error(new RuntimeException("SESSION_NOT_FOUND"));
                        }

                        // ดึง userId และ role (ใช้ String.valueOf ป้องกัน ClassCastException)
                        String userId = String.valueOf(sessionData.get("user_id"));
                        String role = String.valueOf(sessionData.get("role"));

                        // 3. 🔐 ตรวจสอบสิทธิ์ Admin (ถ้าเข้า Path /admin หรือ /api/admin)
                        if (path.contains("/admin")) {
                            if (!"ADMIN".equals(role)) {
                                System.out.println("LOG: Access Denied for User " + userId + " (Role: " + role + ")");
                                return onError(exchange, "Forbidden: Admin Access Only", HttpStatus.FORBIDDEN);
                            }
                        }

                        // 4. ส่งต่อข้อมูลไป Downstream Services
                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .header("X-USER-ID", userId)
                                .header("X-USER-ROLE", role)
                                .build();

                        return chain.filter(exchange.mutate().request(request).build());
                    })
                    .onErrorResume(err -> {
                        String errMsg = err.getMessage();
                        if ("SESSION_NOT_FOUND".equals(errMsg)) {
                            return onError(exchange, "Session expired or not found", HttpStatus.UNAUTHORIZED);
                        }
                        return onError(exchange, "Unauthorized: " + errMsg, HttpStatus.UNAUTHORIZED);
                    });
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        String body = String.format("{\"error\": \"%s\", \"status\": %d}", err, status.value());
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }
}