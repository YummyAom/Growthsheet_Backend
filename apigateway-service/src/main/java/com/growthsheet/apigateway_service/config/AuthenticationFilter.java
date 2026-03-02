package com.growthsheet.apigateway_service.config;

import org.springframework.beans.factory.annotation.Value;
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
import java.util.Map;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    @Value("${SERVICE_TOKEN}")
    private String serviceToken;
    
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public AuthenticationFilter(ReactiveRedisTemplate<String, Object> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    // --- เพิ่มคลาสนี้เข้าไปด้านใน ---
    public static class Config {
        // ตอนนี้ปล่อยว่างไว้ก่อนได้ครับ
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getPath().toString();
            System.out.println("DEBUG [1]: Incoming request to path: " + path);

            // ===============================
            // 1) CHECK INTERNAL SERVICE TOKEN
            // ===============================
            String internalToken = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-INTERNAL-TOKEN");

            if (internalToken != null && internalToken.equals(serviceToken)) {
                System.out.println("DEBUG [INTERNAL]: Internal service call allowed");

                ServerHttpRequest request = exchange.getRequest().mutate()
                        .header("X-USER-ID", "system")
                        .header("X-USER-ROLE", "SYSTEM")
                        .build();

                return chain.filter(exchange.mutate().request(request).build());
            }

            // ===============================
            // 2) USER AUTH (REDIS)
            // ===============================
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("DEBUG [Error]: Missing or invalid Authorization header");
                return onError(exchange, "Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            String accessToken = authHeader.substring(7);
            String redisKey = "access_token:" + accessToken;
            System.out.println("DEBUG [2]: Looking for Redis Key: " + redisKey);

            return redisTemplate.opsForHash().entries(redisKey)
                    .collectMap(ev -> ev.getKey().toString(), Map.Entry::getValue)
                    .doOnNext(data -> System.out.println("DEBUG [3]: Data found in Redis -> " + data))
                    .switchIfEmpty(Mono.defer(() -> {
                        System.out.println("DEBUG [Error]: Key not found in Redis or Session expired");
                        return Mono.error(new RuntimeException("SESSION_NOT_FOUND"));
                    }))
                    .flatMap(sessionData -> {
                        if (sessionData.get("user_id") == null) {
                            System.out.println("DEBUG [Error]: user_id is missing in session data");
                            return onError(exchange, "Invalid Session Data", HttpStatus.UNAUTHORIZED);
                        }

                        System.out.println("DEBUG [4]: Auth Success. User: " + sessionData.get("user_id"));

                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .header("X-USER-ID", sessionData.get("user_id").toString())
                                .header("X-USER-ROLE", sessionData.get("role").toString())
                                .build();

                        return chain.filter(exchange.mutate().request(request).build());
                    })
                    .doOnError(err -> System.err.println("DEBUG [Critical Error]: " + err.getMessage()))
                    .onErrorResume(
                            err -> onError(exchange, "Unauthorized: " + err.getMessage(), HttpStatus.UNAUTHORIZED));
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        // ตั้งค่า Content-Type เป็น JSON เพื่อให้ Postman อ่านง่าย
        response.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        String message = String.format("{\"error\": \"%s\", \"status\": %d}", err, status.value());
        byte[] bytes = message.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // ใช้คำสั่งนี้เพื่อแก้ปัญหา Unchecked Conversion
        return response.writeWith(
                Mono.fromSupplier(() -> response.bufferFactory().wrap(bytes)));
    }
}