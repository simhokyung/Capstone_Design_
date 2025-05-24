package com.example.airqualityplatform.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @Builder
public class ForecastResponseDto {
    private String regionName;
    private List<HourlyForecastDto> hourly;
}