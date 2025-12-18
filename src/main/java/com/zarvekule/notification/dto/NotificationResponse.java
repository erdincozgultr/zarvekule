package com.zarvekule.notification.dto;

import com.zarvekule.notification.enums.NotificationType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private String relatedLink;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
}