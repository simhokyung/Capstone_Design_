package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.NotificationRequestDto;
import com.example.airqualityplatform.dto.response.NotificationResponseDto;
import com.example.airqualityplatform.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDto> createNotification(
            @Valid @RequestBody NotificationRequestDto dto) {
        NotificationResponseDto created = notificationService.createNotification(dto);
        URI location = URI.create("/notifications/" + created.getNotificationId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDto> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
