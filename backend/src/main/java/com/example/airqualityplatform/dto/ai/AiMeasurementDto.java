// src/main/java/com/example/airqualityplatform/dto/ai/AiMeasurementDto.java
package com.example.airqualityplatform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;      // ← Instant 대신 Date
import java.lang.Double;
import java.lang.Long;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiMeasurementDto {
    private Long    measurementId;
    private Long    sensorId;
    private Date    timestamp;      // ← Instant → Date
    private Double  temperature;
    private Double  humidity;
    private Double  co2;
    private Double  voc;
    private Double  pm25_b;
    private Double  pm25_m;
    private Double  pm25_t;
    private Double  pm100_b;
    private Double  pm100_m;
    private Double  pm100_t;
}
