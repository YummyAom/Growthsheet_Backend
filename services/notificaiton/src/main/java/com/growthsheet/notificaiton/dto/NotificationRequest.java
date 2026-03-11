package com.growthsheet.notificaiton.dto;

import java.util.UUID;

@lombok.Data // สำหรับ Getter/Setter
public class NotificationRequest {
    private UUID userId;
    private String title;
    private String message;
}