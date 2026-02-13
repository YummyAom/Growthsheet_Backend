package com.growthsheet.user_service.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

@Service
public class TokenService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public TokenService(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> saveRefreshToken(UUID userId, String refreshToken) {
        return redisTemplate.opsForValue()
                .set("refresh:" + userId, refreshToken, Duration.ofDays(7));
    }

    public Mono<Object> getRefreshToken(UUID userId) {
        return redisTemplate.opsForValue()
                .get("refresh:" + userId);
    }

    public Mono<Boolean> deleteRefreshToken(UUID userId) {
        return redisTemplate.delete("refresh:" + userId)
                .map(deleted -> deleted > 0);
    }
}
