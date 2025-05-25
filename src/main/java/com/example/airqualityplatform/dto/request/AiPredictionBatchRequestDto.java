// src/main/java/com/example/airqualityplatform/dto/request/AiPredictionBatchRequestDto.java
package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiPredictionBatchRequestDto {

    /** ISO-8601 UTC timestamp (60분 예측 전체의 기준 시각) */
    @NotBlank(message = "timestamp를 입력하세요.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
            message = "timestamp 포맷은 yyyy-MM-dd'T'HH:mm:ss'Z' 이어야 합니다.")
    private String timestamp;

    /** 센서별 예측 리스트 (각 항목당 60개 값) */
    @NotEmpty(message = "predictions를 하나 이상 포함해야 합니다.")
    private List<SensorPredictionRequestDto> predictions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SensorPredictionRequestDto {
        @NotNull(message = "sensorId를 입력하세요.")
        private Long sensorId;

        @NotBlank(message = "deviceId를 입력하세요.")
        private String deviceId;

        @NotEmpty(message = "voc 예측값 리스트를 입력하세요.")
        private List<@NotNull Double> voc;

        @NotEmpty(message = "temperature 예측값 리스트를 입력하세요.")
        private List<@NotNull Double> temperature;

        @NotEmpty(message = "humidity 예측값 리스트를 입력하세요.")
        private List<@NotNull Double> humidity;

        @NotEmpty(message = "co2 예측값 리스트를 입력하세요.")
        private List<@NotNull Double> co2;

        @NotEmpty(message = "pm25_t 예측값 리스트를 입력하세요.")
        private List<@NotNull Double> pm25_t;

        @NotEmpty(message = "pm100_t 예측값 리스트를 입력하세요.")
        private List<@NotNull Double> pm100_t;
    }
}
