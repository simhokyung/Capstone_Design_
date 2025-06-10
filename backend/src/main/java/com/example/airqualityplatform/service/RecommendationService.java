package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.response.PurifierRecommendation;
import com.example.airqualityplatform.repository.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RecommendationService {

    @Autowired
    private MeasurementRepository measurementRepository;

    // 기준 PM2.5 값 (참조값, 예: 25㎍/㎥)
    private static final double PM_REF = 25.0;

    // 임계치 차이 (예: 최대 측정값과 평균의 차이가 10㎍/㎥ 이상이면 추천)
    private static final double THRESHOLD_DIFFERENCE = 10.0;

    /**
     * 주어진 roomId에 대해, 지난 'days'일간의 측정 데이터를 분석하여,
     * 가장 높은 PM2.5 값을 가진 센서의 좌표를 추천합니다.
     *
     * @param roomId 대상 Room ID
     * @param days   분석할 기간(일)
     * @return PurifierRecommendation DTO
     */
    public PurifierRecommendation getPurifierRecommendation(Long roomId, int days) {
        // 기간 계산: 현재 시각과 (현재 - days) 시각
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_YEAR, -days);
        Date startDate = cal.getTime();

        // 주어진 roomId에 대해 지난 기간의 측정 데이터 조회
        List<Measurement> measurements = measurementRepository
                .findBySensor_Room_RoomIdAndTimestampBetweenOrderByTimestampAsc(roomId, startDate, now);
        if (measurements.isEmpty()) {
            throw new IllegalArgumentException("No measurement data found for room id: " + roomId);
        }

        double totalPm25 = 0.0;
        int count = 0;
        double maxPm25 = Double.MIN_VALUE;
        Sensor maxSensor = null;
        Date maxTimestamp = null;
        for (Measurement m : measurements) {
            double pm25 = m.getPm25_m() != null ? m.getPm25_m() : 0.0;
            totalPm25 += pm25;
            count++;
            if (pm25 > maxPm25) {
                maxPm25 = pm25;
                maxSensor = m.getSensor();
                maxTimestamp = m.getTimestamp();
            }
        }
        double averagePm25 = totalPm25 / count;
        String message;
        if ((maxPm25 - averagePm25) >= THRESHOLD_DIFFERENCE) {
            message = "추천: 공기청정기를 설치하세요. 최대 PM2.5: " + maxPm25 + "㎍/㎥, 평균 PM2.5: " + averagePm25 + "㎍/㎥.";
        } else {
            message = "현재 데이터로는 공기청정기 설치 추천이 필요하지 않습니다. 최대 PM2.5: " + maxPm25 + "㎍/㎥, 평균 PM2.5: " + averagePm25 + "㎍/㎥.";
        }

        if (maxSensor == null) {
            throw new IllegalArgumentException("No sensor data available for recommendation.");
        }

        return new PurifierRecommendation(
                maxSensor.getSensorId(),
                maxSensor.getXCoordinate(),
                maxSensor.getYCoordinate(),
                maxPm25,
                averagePm25,
                message,
                maxTimestamp
        );
    }
}
