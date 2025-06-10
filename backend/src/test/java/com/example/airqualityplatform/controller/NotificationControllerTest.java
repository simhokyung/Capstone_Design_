package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.response.NotificationResponseDto;
import com.example.airqualityplatform.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateNotification() throws Exception {
        // Notification 생성 요청 테스트 – 여기서는 POST 요청으로 생성한다고 가정
        NotificationResponseDto responseDto = new NotificationResponseDto();
        responseDto.setNotificationId(1L);
        responseDto.setUserId(5L);
        responseDto.setTitle("Test Alert");
        responseDto.setMessage("This is a test notification.");
        responseDto.setRead(false);
        responseDto.setCreatedAt(new Date());

        when(notificationService.createNotification(ArgumentMatchers.any()))
                .thenReturn(responseDto);

        // 일반적으로 요청 DTO와 응답 DTO는 다를 수 있으므로 요청 데이터를 맞춰 작성해야 합니다.
        // 여기서는 테스트 목적으로 간단하게 responseDto를 그대로 요청 데이터로 사용
        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(1L))
                .andExpect(jsonPath("$.title").value("Test Alert"));
    }

    @Test
    public void testGetNotificationsByUser() throws Exception {
        NotificationResponseDto n1 = new NotificationResponseDto();
        n1.setNotificationId(1L);
        n1.setUserId(5L);
        n1.setTitle("Alert1");
        n1.setMessage("Message1");
        n1.setRead(false);
        n1.setCreatedAt(new Date());

        NotificationResponseDto n2 = new NotificationResponseDto();
        n2.setNotificationId(2L);
        n2.setUserId(5L);
        n2.setTitle("Alert2");
        n2.setMessage("Message2");
        n2.setRead(false);
        n2.setCreatedAt(new Date());

        when(notificationService.getNotificationsByUser(eq(5L)))
                .thenReturn(Arrays.asList(n1, n2));

        mockMvc.perform(get("/notifications/user/{userId}", 5L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value(1L))
                .andExpect(jsonPath("$[0].title").value("Alert1"))
                .andExpect(jsonPath("$[1].notificationId").value(2L))
                .andExpect(jsonPath("$[1].message").value("Message2"));
    }

    @Test
    public void testGetNotificationById() throws Exception {
        NotificationResponseDto responseDto = new NotificationResponseDto();
        responseDto.setNotificationId(1L);
        responseDto.setUserId(5L);
        responseDto.setTitle("Alert");
        responseDto.setMessage("Test notification");
        responseDto.setRead(false);
        responseDto.setCreatedAt(new Date());

        when(notificationService.getNotificationById(eq(1L)))
                .thenReturn(responseDto);  // ✅ 수정: Optional이 아닌 단일 객체

        mockMvc.perform(get("/notifications/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(1L))
                .andExpect(jsonPath("$.title").value("Alert"));
    }

    @Test
    public void testMarkNotificationAsRead() throws Exception {
        NotificationResponseDto responseDto = new NotificationResponseDto();
        responseDto.setNotificationId(1L);
        responseDto.setUserId(5L);
        responseDto.setTitle("Alert");
        responseDto.setMessage("Test notification");
        responseDto.setRead(true);
        responseDto.setCreatedAt(new Date());

        when(notificationService.markNotificationAsRead(eq(1L)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/notifications/{id}/read", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    public void testDeleteNotification() throws Exception {
        // DELETE 요청 시 상태 코드만 검증
        mockMvc.perform(delete("/notifications/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
