package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.Notification;
import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.mapper.NotificationMapper;
import com.example.airqualityplatform.dto.request.NotificationRequestDto;
import com.example.airqualityplatform.dto.response.NotificationResponseDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class NotificationMapperTest {

    @Test
    public void testToEntity() {
        // NotificationRequestDto 객체 생성 및 값 설정
        NotificationRequestDto req = new NotificationRequestDto();
        req.setUserId(5L);
        req.setTitle("Test Title");
        req.setMessage("Test message content");

        // DTO → Notification 엔티티 변환 (업데이트 대상은 null)
        Notification notification = NotificationMapper.toEntity(req, null);
        assertNotNull(notification);
        assertEquals("Test Title", notification.getTitle());
        assertEquals("Test message content", notification.getMessage());
        // user 정보는 Service 계층에서 처리하므로 여기서는 null일 수 있음.
    }

    @Test
    public void testToResponseDto() {
        // Notification 엔티티 생성 및 값 설정
        Notification notification = new Notification();
        notification.setNotificationId(10L);
        User user = new User();
        user.setUserId(5L);
        notification.setUser(user);
        notification.setTitle("Alert");
        notification.setMessage("You have a new alert");
        notification.setRead(false);
        Date now = new Date();
        notification.setCreatedAt(now);

        NotificationResponseDto dto = NotificationMapper.toResponseDto(notification);
        assertNotNull(dto);
        assertEquals(10L, dto.getNotificationId());
        assertEquals(5L, dto.getUserId());
        assertEquals("Alert", dto.getTitle());
        assertEquals("You have a new alert", dto.getMessage());
        assertFalse(dto.getRead());
        assertEquals(now, dto.getCreatedAt());
    }
}
