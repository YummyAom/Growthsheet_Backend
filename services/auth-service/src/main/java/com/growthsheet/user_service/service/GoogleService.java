package com.growthsheet.user_service.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.growthsheet.user_service.entity.User;
import com.growthsheet.user_service.entity.UserRole;
import com.growthsheet.user_service.respository.UserRepository;
import com.growthsheet.user_service.security.Jwtutil;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class GoogleService {

    private final UserRepository userRepository;
    private final Jwtutil jwtUtil;
    private final AuthService authService;
    private final GoogleService googleService;

    public GoogleService(
            UserRepository userRepository,
            Jwtutil jwtUtil,
            AuthService authService,
            GoogleService googleService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.googleService = googleService;
    }

    @Value("${google.client-id}")
    private String googleClientId;
    @Value("${google.android-id}")
    private String androidClientId;
    @Value("${google.ios-id}")
    private String iosClientId;

    @Value("${GOOLE_SECRET}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public String handleGoogleLogin(String code) {

        String tokenUrl = "https://oauth2.googleapis.com/token";

        Map<String, String> body = new HashMap<>();
        body.put("code", code);
        body.put("client_id", googleClientId);
        body.put("client_secret", clientSecret);
        body.put("redirect_uri", "http://localhost:8080/auth/google/callback");
        body.put("grant_type", "authorization_code");

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, body, Map.class);

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> userResponse = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                entity,
                Map.class);

        Map user = userResponse.getBody();

        String email = (String) user.get("email");
        String name = (String) user.get("name");

        // TODO: check user in DB
        // ถ้าไม่มี → create user

        // create JWT
        String jwt = "";

        return jwt;
    }

    public Mono<Map<String, Object>> googleLogin(String idToken) {

        return Mono.fromCallable(() -> {

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(List.of(
                            googleClientId,
                            androidClientId,
                            iosClientId))
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