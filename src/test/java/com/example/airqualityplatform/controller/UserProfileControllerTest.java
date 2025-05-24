package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.response.UserResponseDto;
import com.example.airqualityplatform.service.UserService;
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

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(UserProfileController.class)
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * GET /users/{userId}/location 테스트
     */
    @Test
    public void testGetUserLocation() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setEmail("testuser@example.com");
        user.setUsername("TestUser");
        user.setAddress("서울특별시 강남구");
        user.setLatitude(37.5);
        user.setLongitude(127.0);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        // 실제 서비스는 Optional<User>를 반환하므로, 테스트에서는 Optional.of(user)로 설정
        when(userService.getUserById(eq(userId))).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/{userId}/location", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.username").value("TestUser"))
                .andExpect(jsonPath("$.address").value("서울특별시 강남구"));
    }

    /**
     * PUT /users/{userId}/location 테스트
     */
    @Test
    public void testUpdateUserLocation() throws Exception {
        Long userId = 1L;
        // 업데이트 요청 DTO 생성
        UserLocationUpdateRequestDto updateDto = new UserLocationUpdateRequestDto();
        updateDto.setAddress("서울시 강남구 신사동");
        updateDto.setLatitude(37.55);
        updateDto.setLongitude(127.01);

        // 업데이트 후 반환할 UserResponseDto 생성
        UserResponseDto updatedUser = new UserResponseDto();
        updatedUser.setUserId(userId);
        updatedUser.setEmail("testuser@example.com");
        updatedUser.setUsername("TestUser");
        updatedUser.setAddress("서울시 강남구 신사동");
        updatedUser.setLatitude(37.55);
        updatedUser.setLongitude(127.01);
        updatedUser.setCreatedAt(new Date());
        updatedUser.setUpdatedAt(new Date());

        when(userService.updateUserLocation(eq(userId), ArgumentMatchers.any(UserLocationUpdateRequestDto.class)))
                .thenReturn(updatedUser);

        // PUT 요청 수행 및 결과 검증
        mockMvc.perform(put("/users/{userId}/location", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.address").value("서울시 강남구 신사동"))
                .andExpect(jsonPath("$.latitude").value(37.55))
                .andExpect(jsonPath("$.longitude").value(127.01));
    }
}
