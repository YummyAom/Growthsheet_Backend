package com.growthsheet.notificaiton.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

// import com.growthsheet.notificaiton.entity.NotificationType;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private UUID id;
    // private NotificationType type;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}