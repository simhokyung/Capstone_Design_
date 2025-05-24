package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.ai.PredictionResponse;
import com.example.airqualityplatform.ai.PredictionService;
import com.example.airqualityplatform.config.SecurityConfigTest;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AIControllerTest는 AIController에 대한 MockMvc 테스트 코드입니다.
 * AIController가 /ai/predictions?sensorId=...&hours=... 등의 엔드포인트를 처리한다고 가정합니다.
 */
@WebMvcTest(AIController.class)
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
public class AIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PredictionService predictionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetPredictions() throws Exception {
        // GIVEN: Mock된 PredictionResponse 리스트
        PredictionResponse p1 = new PredictionResponse(
                1L, "2025-03-16T10:00:00Z", 22.0, 45.0, 800.0, 0.12, 50.0, 60.0
        );
        PredictionResponse p2 = new PredictionResponse(
                1L, "2025-03-16T11:00:00Z", 23.0, 46.0, 810.0, 0.13, 51.0, 61.0
        );

        when(predictionService.predictAirQuality(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(3)))
                .thenReturn(Arrays.asList(p1, p2));

        // WHEN & THEN: GET /ai/predictions?sensorId=1&hours=3
        mockMvc.perform(get("/ai/predictions")
                        .param("sensorId", "1")
                        .param("hours", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // 검증: 배열 크기가 2개, 첫 번째 원소 필드 검증 등
                .andExpect(jsonPath("$[0].sensorId").value(1L))
                .andExpect(jsonPath("$[1].predictedTemperature").value(23.0));
    }
}
