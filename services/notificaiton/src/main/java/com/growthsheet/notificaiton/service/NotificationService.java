package com.growthsheet.notificaiton.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.growthsheet.notificaiton.entity.Notification;
import com.growthsheet.notificaiton.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(
            NotificationRepository notificationRepo,
            SimpMessagingTemplate messagingTemplate) {

        this.notificationRepo = notificationRepo;
        this.messagingTemplate = messagingTemplate;
    }

    public Notification create(UUID userId,
            String title,
            String message) {

        System.out.println("===== CREATE NOTIFICATION =====");
       
        System.out.println("userId = " + userId);
        System.out.println("title = " + title);
        System.out.println("message = " + message);

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepo.save(notification);

        System.out.println("Notification saved with id = " + notification.getId());

        System.out.println("Sending WS to user = " + userId);

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification);

        System.out.println("WS send completed");

        return notification;
    }

    public Page<Notification> getUserNotifications(
            UUID userId,
            Pageable pageable) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public long countUnread(UUID userId) {
        return notificationRepo.countByUserIdAndIsReadFalse(userId);
    }

    public void markRead(UUID notificaitonId) {
        Notification notification = notificationRepo.findById(notificaitonId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        notificationRepo.save(notification);
    }

    public void markAllRead(UUID userId) {
        var list = notificationRepo.findByUserIdAndIsReadFalse(userId);
        list.forEach(
                n -> n.setIsRead(true));

        notificationRepo.saveAll(list);
    }

}
