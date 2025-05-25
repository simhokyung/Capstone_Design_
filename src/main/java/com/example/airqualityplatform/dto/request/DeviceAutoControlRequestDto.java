// src/main/java/com/example/airqualityplatform/dto/request/DeviceAutoControlRequestDto.java
package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceAutoControlRequestDto {

    @NotNull(message = "Room ID를 입력하세요.")
    private Long roomId;

    @NotNull(message = "PM2.5 임계값을 입력하세요.")
    private Double pm25Threshold;

    @NotNull(message = "CO₂ 임계값을 입력하세요.")
    private Double co2Threshold;

    @NotNull(message = "PM10 임계값을 입력하세요.")
    private Double pm100Threshold;

    @NotNull(message = "VOC 임계값을 입력하세요.")
    private Double vocThreshold;

}
