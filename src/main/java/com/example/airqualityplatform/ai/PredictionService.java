package com.example.airqualityplatform.ai;

import java.util.List;

public interface PredictionService {
    // sensorId와 예측할 시간 범위(시간 단위)를 입력받아 예측 결과 리스트를 반환합니다.
    List<PredictionResponse> predictAirQuality(Long sensorId, int hours);
}