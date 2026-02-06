package com.growthsheet.apigateway_service.services;

import java.security.Key;
import java.security.PrivateKey;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.growthsheet.apigateway_service.dto.TokenVerifyResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class InternalJwtService {

    private final Key privateKey;

    public InternalJwtService() throws Exception {
        this.privateKey = loadPrivateKey();
    }

    public String createToken(TokenVerifyResponse user) {

        return Jwts.builder()
                .setSubject(user.getUserId())
                .claim("authorities", user.getAuthorities())
                .claim("client_id", user.getClientId())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private PrivateKey loadPrivateKey() {
        // load from keystore / env / vault
        return null;
    }
}