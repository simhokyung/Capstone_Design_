package com.example.airqualityplatform.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PredictionResponse {
    private Long sensorId;
    private String futureTimestamp;      // 예측된 시각 (ISO 8601 형식 권장)
    private Double predictedTemperature; // 예측 온도 (°C)
    private Double predictedHumidity;    // 예측 습도 (%)
    private Double predictedCo2;         // 예측 CO₂ (ppm)
    private Double predictedVoc;         // 예측 VOC 농도
    private Double predictedPm25;        // 예측 PM2.5
    private Double predictedPm10;        // 예측 PM10
}