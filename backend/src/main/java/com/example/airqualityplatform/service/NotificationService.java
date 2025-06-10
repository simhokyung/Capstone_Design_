package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Notification;
import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.request.NotificationRequestDto;
import com.example.airqualityplatform.dto.response.NotificationResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.dto.mapper.NotificationMapper;
import com.example.airqualityplatform.repository.NotificationRepository;
import com.example.airqualityplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public NotificationResponseDto createNotification(NotificationRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "사용자를 찾을 수 없습니다. id: " + dto.getUserId()));
        Notification entity = NotificationMapper.toEntity(dto, null);
        entity.setUser(user);
        Notification saved = notificationRepository.save(entity);
        return NotificationMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다. id: " + userId);
        }
        return notificationRepository.findByUser_UserIdAndIsReadFalse(userId)
                .stream()
                .map(NotificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(NotificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificationResponseDto getNotificationById(Long id) {
        Notification entity = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "알림을 찾을 수 없습니다. id: " + id));
        return NotificationMapper.toResponseDto(entity);
    }

    @Transactional
    public NotificationResponseDto markAsRead(Long id) {
        Notification entity = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "알림을 찾을 수 없습니다. id: " + id));
        entity.setIsRead(true);
        Notification saved = notificationRepository.save(entity);
        return NotificationMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("알림을 찾을 수 없습니다. id: " + id);
        }
        notificationRepository.deleteById(id);
    }

    public void notifyVentilation(String deviceId, int minuteOffset) {
        // TODO: 이메일·푸시·웹소켓 등으로 환기 알림 구현
        System.out.printf("⚠️ Device %s: %d분 후 환기 권장%n", deviceId, minuteOffset);
    }
}