// src/main/java/com/example/airqualityplatform/dto/response/AirQualityResponseDto.java
package com.example.airqualityplatform.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter @Builder
public class AirQualityResponseDto {
    private String regionName;
    private Double latitude;
    private Double longitude;
    private int aqi;
    private Double pm25;
    private Double pm10;
    private Double o3;
    private Double no2;
    private Double temperature;
    private Instant fetchedAt;
}
