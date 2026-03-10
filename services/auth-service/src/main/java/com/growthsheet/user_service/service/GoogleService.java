package com.growthsheet.user_service.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.growthsheet.user_service.entity.User;
import com.growthsheet.user_service.entity.UserRole;
import com.growthsheet.user_service.respository.UserRepository;
import com.growthsheet.user_service.security.Jwtutil;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class GoogleService {

    private final UserRepository userRepository;
    private final Jwtutil jwtUtil;
    private final AuthService authService;

    public GoogleService(
            UserRepository userRepository,
            Jwtutil jwtUtil,
            AuthService authService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Value("${google.client-id}")
    private String googleClientId;

    public Mono<Map<String, Object>> googleLogin(String idToken) {

        return Mono.fromCallable(() -> {

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if (googleIdToken == null) {
                throw new RuntimeException("Invalid Google token");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            Optional<User> optionalUser = userRepository.findByEmail(email);

            User user;

            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {

                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setPassword("GOOGLE_LOGIN");
                user.setRole(UserRole.BUYER);
                user.setEnabled(true);
                user.setUserPhotoUrl(picture);

                user = userRepository.save(user);
            }
            
            return user;

        })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(authService::loginWithUser);
    }
}