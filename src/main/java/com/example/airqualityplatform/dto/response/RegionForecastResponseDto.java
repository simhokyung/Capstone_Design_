// src/main/java/com/example/airqualityplatform/dto/response/RegionForecastResponseDto.java
package com.example.airqualityplatform.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @Builder
public class RegionForecastResponseDto {
    private Long regionId;
    private String regionName;
    private Double latitude;
    private Double longitude;
    private List<HourlyForecastDto> forecast;
}
