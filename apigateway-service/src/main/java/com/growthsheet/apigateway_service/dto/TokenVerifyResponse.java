package com.growthsheet.apigateway_service.dto;

import java.util.List;

public class TokenVerifyResponse {

    private String userId;
    private List<String> authorities;
    private String clientId;

    public TokenVerifyResponse() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}