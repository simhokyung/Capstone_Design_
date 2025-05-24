package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.response.PurifierRecommendation;
import com.example.airqualityplatform.repository.MeasurementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecommendationServiceTest {

    @Mock
    private MeasurementRepository measurementRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPurifierRecommendation() {
        // 테스트 데이터 준비
        Long roomId = 1L;
        int days = 7;
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_YEAR, -days);
        Date startDate = cal.getTime();

        // Sensor 및 Measurement 생성
        Sensor sensor = new Sensor();
        sensor.setSensorId(10L);
        sensor.setXCoordinate(100.0);
        sensor.setYCoordinate(200.0);

        Measurement m1 = new Measurement();
        m1.setSensor(sensor);
        m1.setTimestamp(startDate);
        m1.setPm25(30.0);

        Measurement m2 = new Measurement();
        m2.setSensor(sensor);
        m2.setTimestamp(now);
        m2.setPm25(45.0);

        // 모킹: roomId와 시간 범위에 대해 조회하도록 설정
        when(measurementRepository.findBySensor_Room_RoomIdAndTimestampBetweenOrderByTimestampAsc(
                eq(roomId), any(Date.class), any(Date.class)))
                .thenReturn(Arrays.asList(m1, m2));

        // getPurifierRecommendation 메서드 호출
        PurifierRecommendation recommendation = recommendationService.getPurifierRecommendation(roomId, days);
        assertNotNull(recommendation);
        assertEquals(10L, recommendation.getSensorId());
        // 최대 PM2.5는 45.0, 평균은 (30+45)/2 = 37.5
        assertEquals(45.0, recommendation.getMaxPm25());
        assertEquals(37.5, recommendation.getAveragePm25(), 0.001);
        // 메시지가 예상대로 포함되는지 확인 (예: "추천" 또는 "추천이 필요하지 않습니다." 등)
        assertNotNull(recommendation.getMessage());
        // 최대 측정 시각 확인
        assertEquals(now, recommendation.getTimestamp());

        // 리포지토리 메서드가 한 번 호출되었는지 검증
        verify(measurementRepository, times(1))
                .findBySensor_Room_RoomIdAndTimestampBetweenOrderByTimestampAsc(eq(roomId), any(Date.class), any(Date.class));
    }
}
