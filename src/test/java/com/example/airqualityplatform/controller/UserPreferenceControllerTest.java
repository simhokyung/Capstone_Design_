package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.request.UserPreferenceRequestDto;
import com.example.airqualityplatform.dto.response.UserPreferenceResponseDto;
import com.example.airqualityplatform.service.UserPreferenceService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(UserPreferenceController.class)
public class UserPreferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPreferenceService userPreferenceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetUserPreference() throws Exception {
        Long userId = 1L;
        UserPreferenceResponseDto responseDto = new UserPreferenceResponseDto();
        responseDto.setPreferenceId(100L);
        responseDto.setUserId(userId);
        responseDto.setPm25Threshold(35.0);
        responseDto.setPm10Threshold(50.0);
        responseDto.setCo2Threshold(800.0);
        responseDto.setVocThreshold(0.12);
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        // 서비스 메서드가 단일 객체(UserPreferenceResponseDto)를 반환하도록 설계되어 있으면
        when(userPreferenceService.getPreferenceByUserId(ArgumentMatchers.eq(userId)))
                .thenReturn(Optional.of(responseDto));

        mockMvc.perform(get("/users/{userId}/preferences", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferenceId").value(100L))
                .andExpect(jsonPath("$.pm25Threshold").value(35.0));
    }

    @Test
    public void testCreateOrUpdatePreference() throws Exception {
        Long userId = 1L;
        UserPreferenceRequestDto requestDto = new UserPreferenceRequestDto();
        requestDto.setPm25Threshold(35.0);
        requestDto.setPm10Threshold(50.0);
        requestDto.setCo2Threshold(800.0);
        requestDto.setVocThreshold(0.12);

        UserPreferenceResponseDto responseDto = new UserPreferenceResponseDto();
        responseDto.setPreferenceId(100L);
        responseDto.setUserId(userId);
        responseDto.setPm25Threshold(35.0);
        responseDto.setPm10Threshold(50.0);
        responseDto.setCo2Threshold(800.0);
        responseDto.setVocThreshold(0.12);
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(userPreferenceService.createOrUpdatePreference(ArgumentMatchers.eq(userId),
                ArgumentMatchers.any(UserPreferenceRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/users/{userId}/preferences", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.pm25Threshold").value(35.0));
    }

    @Test
    public void testProcessSurvey() throws Exception {
        Long userId = 1L;
        UserSurveyRequest surveyRequest = new UserSurveyRequest();
        // 예시로 설문 응답 데이터 설정 (예: hasAsthma 여부)
        surveyRequest.setHasAsthma(true);

        // 설문을 통한 Preference 생성 서비스가 엔티티를 반환하거나 DTO를 그대로 반환하도록 가정
        UserPreferenceResponseDto responseDto = new UserPreferenceResponseDto();
        responseDto.setPreferenceId(100L);
        responseDto.setUserId(userId);
        responseDto.setPm25Threshold(25.0);  // 천식 환자 기준치 25
        responseDto.setPm10Threshold(50.0);
        responseDto.setCo2Threshold(800.0);
        responseDto.setVocThreshold(0.12);
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(userPreferenceService.createPreferenceFromSurvey(ArgumentMatchers.eq(userId),
                ArgumentMatchers.any(UserSurveyRequest.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/users/{userId}/preferences/survey", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(surveyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pm25Threshold").value(25.0));
    }
}
