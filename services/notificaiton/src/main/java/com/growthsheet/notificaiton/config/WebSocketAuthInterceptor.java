package com.growthsheet.notificaiton.config;


import org.springframework.http.server.*;
import org.springframework.web.socket.*;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        String userId = request.getHeaders().getFirst("X-USER-ID");

        if (userId == null) {
            return false;
        }

        attributes.put("principal", (Principal) () -> userId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }
}