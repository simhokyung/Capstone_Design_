// src/main/java/com/example/airqualityplatform/dto/response/SensorPredictionDto.java
package com.example.airqualityplatform.dto.response;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SensorPredictionDto {
    private Long sensorId;
    private String deviceId;
    private List<Double> voc;
    private List<Double> temperature;
    private List<Double> humidity;
    private List<Double> co2;
    private List<Double> pm25_t;
    private List<Double> pm100_t;
}
