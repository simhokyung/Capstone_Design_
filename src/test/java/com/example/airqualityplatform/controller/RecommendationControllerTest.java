package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.response.PurifierRecommendation;
import com.example.airqualityplatform.service.RecommendationService;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(RecommendationController.class)
public class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetPurifierRecommendation() throws Exception {
        PurifierRecommendation recommendation = new PurifierRecommendation(
                1L,        // sensorId
                55.0,      // xCoordinate
                75.0,      // yCoordinate
                45.0,      // maxPm25
                37.5,      // averagePm25
                "추천: 공기청정기 설치 필요", // 메시지
                new Date() // 최대 측정 시각
        );

        when(recommendationService.getPurifierRecommendation(eq(1L), anyInt()))
                .thenReturn(recommendation);

        mockMvc.perform(get("/recommendations/purifier")
                        .param("roomId", "1")
                        .param("days", "7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value(1L))
                .andExpect(jsonPath("$.message").value("추천: 공기청정기 설치 필요"));
    }
}
