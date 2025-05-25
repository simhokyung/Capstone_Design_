// src/main/java/com/example/airqualityplatform/dto/ai/AiPredictionRequestDto.java
package com.example.airqualityplatform.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter @Setter
public class AiPredictionRequestDto {

    /** AI가 예측을 시작한 시각 (ISO-8601, UTC) */
    @NotNull
    private Instant timestamp;

    /** 센서별 예측 결과 리스트 (반드시 1개 이상) */
    @Valid
    @NotEmpty
    private List<SensorPredictionDto> predictions;

    @Getter @Setter
    public static class SensorPredictionDto {
        /** 내부 센서 ID */
        @NotNull
        private Long sensorId;

        /** SmartThings deviceId (UUID) */
        @NotNull
        private String deviceId;

        /** 60분치 예측값 */
        @Valid
        @NotNull
        @JsonProperty("prediction")
        private PredictionDetailDto detail;
    }

    @Getter @Setter
    public static class PredictionDetailDto {
        /** 1분 뒤부터 60분 뒤까지 VOC(ppb) 예측치 */
        @NotEmpty @Size(min = 60, max = 60)
        private List<Double> voc;

        /** 1분 뒤부터 60분 뒤까지 온도(°C) 예측치 */
        @NotEmpty @Size(min = 60, max = 60)
        private List<Double> temperature;

        /** 1분 뒤부터 60분 뒤까지 습도(%) 예측치 */
        @NotEmpty @Size(min = 60, max = 60)
        private List<Double> humidity;

        /** 1분 뒤부터 60분 뒤까지 CO₂(ppm) 예측치 */
        @NotEmpty @Size(min = 60, max = 60)
        private List<Double> co2;

        /** 1분 뒤부터 60분 뒤까지 PM2.5(μg/m³) 추세치 */
        @NotEmpty @Size(min = 60, max = 60)
        private List<Double> pm25_t;

        /** 1분 뒤부터 60분 뒤까지 PM10(μg/m³) 추세치 */
        @NotEmpty @Size(min = 60, max = 60)
        private List<Double> pm100_t;
    }
}
