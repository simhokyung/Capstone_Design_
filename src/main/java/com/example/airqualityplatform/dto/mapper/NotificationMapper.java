package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.Notification;
import com.example.airqualityplatform.dto.request.NotificationRequestDto;
import com.example.airqualityplatform.dto.response.NotificationResponseDto;

public class NotificationMapper {

    public static Notification toEntity(NotificationRequestDto dto, Notification entity) {
        if (entity == null) entity = new Notification();
        entity.setTitle(dto.getTitle());
        entity.setMessage(dto.getMessage());
        entity.setType(dto.getType());
        // isRead 기본 false
        return entity;
    }

    public static NotificationResponseDto toResponseDto(Notification entity) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setNotificationId(entity.getNotificationId());
        dto.setUserId(entity.getUser().getUserId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setType(entity.getType());
        dto.setIsRead(entity.getIsRead());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
