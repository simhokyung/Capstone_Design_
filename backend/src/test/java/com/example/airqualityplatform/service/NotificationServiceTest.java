package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Notification;
import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.mapper.NotificationMapper;
import com.example.airqualityplatform.dto.request.NotificationRequestDto;
import com.example.airqualityplatform.dto.response.NotificationResponseDto;
import com.example.airqualityplatform.repository.NotificationRepository;
import com.example.airqualityplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateNotification() {
        NotificationRequestDto req = new NotificationRequestDto();
        req.setUserId(5L);
        req.setTitle("Alert");
        req.setMessage("Test alert message");

        // 모킹: 사용자 조회
        User user = new User();
        user.setUserId(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        // DTO -> 엔티티 변환 (NotificationMapper 사용)
        Notification notification = NotificationMapper.toEntity(req, null);
        notification.setUser(user);
        notification.setNotificationId(10L);
        Date now = new Date();
        notification.setCreatedAt(now);

        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationResponseDto response = notificationService.createNotification(req);
        assertNotNull(response);
        assertEquals(10L, response.getNotificationId());
        assertEquals(5L, response.getUserId());
        assertEquals("Alert", response.getTitle());
        verify(userRepository, times(1)).findById(5L);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testGetNotificationsByUser() {
        User user = new User();
        user.setUserId(5L);
        Notification n1 = new Notification();
        n1.setNotificationId(10L);
        n1.setUser(user);
        n1.setTitle("Alert1");
        n1.setMessage("Message1");
        n1.setRead(false);
        n1.setCreatedAt(new Date());

        Notification n2 = new Notification();
        n2.setNotificationId(11L);
        n2.setUser(user);
        n2.setTitle("Alert2");
        n2.setMessage("Message2");
        n2.setRead(false);
        n2.setCreatedAt(new Date());

        when(notificationRepository.findByUser_UserIdAndReadFalse(5L)).thenReturn(Arrays.asList(n1, n2));
        var list = notificationService.getNotificationsByUser(5L);
        assertEquals(2, list.size());
        verify(notificationRepository, times(1)).findByUser_UserIdAndReadFalse(5L);
    }

    @Test
    public void testMarkNotificationAsRead() {
        Notification n = new Notification();
        n.setNotificationId(10L);
        User user = new User();
        user.setUserId(5L);
        n.setUser(user);
        n.setTitle("Alert");
        n.setMessage("Message");
        n.setRead(false);
        n.setCreatedAt(new Date());

        when(notificationRepository.findById(10L)).thenReturn(Optional.of(n));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponseDto response = notificationService.markNotificationAsRead(10L);
        assertTrue(response.getRead());
        verify(notificationRepository, times(1)).findById(10L);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testDeleteNotification() {
        doNothing().when(notificationRepository).deleteById(10L);
        notificationService.deleteNotification(10L);
        verify(notificationRepository, times(1)).deleteById(10L);
    }
}
