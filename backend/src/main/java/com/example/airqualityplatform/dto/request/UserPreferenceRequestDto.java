package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserPreferenceRequestDto {
    @NotNull(message = "PM2.5 기준치를 입력하세요.")
    private Double pm25Threshold;

    @NotNull(message = "PM100 기준치를 입력하세요.")
    private Double pm100Threshold;

    @NotNull(message = "CO₂ 기준치를 입력하세요.")
    private Double co2Threshold;

    @NotNull(message = "VOC 기준치를 입력하세요.")
    private Double vocThreshold;
}
