package com.growthsheet.notificaiton.config;


import java.security.Principal;
import java.util.UUID;

public class UserPrincipal implements Principal {

    private final UUID userId;

    public UserPrincipal(UUID userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return userId.toString();
    }
}