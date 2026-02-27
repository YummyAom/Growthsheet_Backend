package com.growthsheet.notificaiton.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.growthsheet.notificaiton.entity.Notification;
import com.growthsheet.notificaiton.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepo;

    public Notification create(
            UUID userId,
            String title,
            String message) {

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notificationRepo.save(notification);

        return notification;
    }

    public Page<Notification> getUserNotifications(
        UUID userId,
        Pageable pageable
    ){
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public long countUnread(UUID userId) {
        return notificationRepo.countByUserIdAndIsReadFalse(userId);
    }

    public void markRead(UUID notificaitonId){
        Notification notification = notificationRepo.findById(notificaitonId)
        .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        notificationRepo.save(notification);
    }

    public void markAllRead(UUID userId){
        var list = notificationRepo.findByUserIdAndIsReadFalse(userId);
        list.forEach(
            n -> n.setIsRead(true)
        );

        notificationRepo.saveAll(list);
    }

    

}
