package com.growthsheet.user_service.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public String hashPassword(String rawPass){
        return encoder.encode(rawPass);
    }

    public boolean matches(String rawPass, String hashPass){
        return encoder.matches(rawPass, hashPass);
    }
}
