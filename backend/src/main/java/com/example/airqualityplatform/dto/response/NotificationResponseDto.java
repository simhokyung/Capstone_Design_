package com.example.airqualityplatform.dto.response;

import com.example.airqualityplatform.domain.Notification;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class NotificationResponseDto {
    private Long notificationId;
    private Long userId;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}