package com.growthsheet.notificaiton.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.notificaiton.entity.Notification;
import com.growthsheet.notificaiton.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @PostMapping
    public Notification create(
            @RequestParam UUID userId,
            @RequestParam String title,
            @RequestParam String message) {
        return notificationService.create(userId, title, message);
    }

    @GetMapping("/{userId}")
    public Page<Notification> getUserNotifications(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return notificationService.getUserNotifications(userId, PageRequest.of(page, size));
    }

    @GetMapping("/{userId}/unread-count")
    public long countUnread(@RequestHeader("X-USER-ID") UUID userId) {
        return notificationService.countUnread(userId);
    }

    @PatchMapping("/{id}/read")
    public String markAsRead(@RequestHeader("X-USER-ID") UUID userId) {
        notificationService.markRead(userId);
        return "Notification marked as read";
    }

    @PatchMapping("/{userId}/read-all")
    public String markAllAsRead(@RequestHeader("X-USER-ID") UUID userId) {
        notificationService.markAllRead(userId);
        return "All notifications marked as read";
    }
}
