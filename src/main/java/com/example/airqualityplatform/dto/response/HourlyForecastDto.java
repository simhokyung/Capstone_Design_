// src/main/java/com/example/airqualityplatform/dto/response/HourlyForecastDto.java
package com.example.airqualityplatform.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class HourlyForecastDto {
    /** ISO timestamp, e.g. "2025-05-23T14:00:00" */
    private String datetime;
    private Double temperature;
    private Double pm25;
    private Double pm10;
    private Double o3;
    private Double no2;
}
