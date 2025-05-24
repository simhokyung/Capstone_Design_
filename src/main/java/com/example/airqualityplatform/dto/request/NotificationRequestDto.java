package com.example.airqualityplatform.dto.request;

import com.example.airqualityplatform.domain.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NotificationRequestDto {

    @NotNull(message = "userId를 입력하세요.")
    private Long userId;

    @NotBlank(message = "title을 입력하세요.")
    @Size(max = 100, message = "title은 최대 100자까지 가능합니다.")
    private String title;

    @NotBlank(message = "message를 입력하세요.")
    private String message;

    @NotNull(message = "type을 입력하세요.")
    private Notification.NotificationType type;
}