package com.growthsheet.notificaiton.config;

import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class StompAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String userId = accessor.getFirstNativeHeader("X-USER-ID");

            System.out.println("WS CONNECT HEADER userId = " + userId);

            if (userId != null) {
                accessor.setUser(new UserPrincipal(UUID.fromString(userId)));

                System.out.println("Principal SET = " + userId);
            }
        }

        return message;
    }
}