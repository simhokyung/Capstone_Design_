package com.example.airqualityplatform.repository;


import com.example.airqualityplatform.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자(userId)에 대한 미읽은 알림을 조회하는 메서드 예시
    List<Notification> findByUser_UserIdAndIsReadFalse(Long userId);
}