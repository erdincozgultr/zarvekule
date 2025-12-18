package com.zarvekule.notification.service;

import com.zarvekule.notification.dto.NotificationResponse;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.user.entity.User;

import java.util.List;

public interface NotificationService {

    void createNotification(User recipient, String title, String message, NotificationType type, String relatedLink);

    List<NotificationResponse> getMyNotifications(String username);
    long getUnreadCount(String username);
    void markAsRead(String username, Long notificationId);
    void markAllAsRead(String username);
}