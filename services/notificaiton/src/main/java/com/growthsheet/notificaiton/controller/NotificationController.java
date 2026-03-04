package com.growthsheet.notificaiton.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.growthsheet.notificaiton.entity.Notification;
import com.growthsheet.notificaiton.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ===== create notification (internal call เช่น จาก payment-service) =====
    @PostMapping
    public Notification create(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestParam String title,
            @RequestParam String message
    ) {
        return notificationService.create(userId, title, message);
    }

    // ===== get notifications (history) =====
    @GetMapping
    public Page<Notification> getUserNotifications(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return notificationService.getUserNotifications(
                userId,
                PageRequest.of(page, size)
        );
    }

    // ===== unread count =====
    @GetMapping("/unread-count")
    public long countUnread(
            @RequestHeader("X-USER-ID") UUID userId) {

        return notificationService.countUnread(userId);
    }

    // ===== mark single notification as read =====
    @PatchMapping("/{id}/read")
    public String markAsRead(
            @PathVariable UUID id) {

        notificationService.markRead(id);
        return "Notification marked as read";
    }

    // ===== mark all as read =====
    @PatchMapping("/read-all")
    public String markAllAsRead(
            @RequestHeader("X-USER-ID") UUID userId) {

        notificationService.markAllRead(userId);
        return "All notifications marked as read";
    }
}