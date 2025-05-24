package com.example.airqualityplatform.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SensorRequestDto {

    @NotNull(message = "외부 식별자(sensorId)를 입력하세요.")
    private Long sensorId;

    @NotNull(message = "Room ID를 입력하세요.")
    private Long roomId;

    @NotBlank(message = "센서 이름(sensorName)을 입력하세요.")
    @Size(max = 100)
    private String sensorName;

    @NotBlank(message = "센서 타입(sensorType)을 입력하세요.")
    @Size(max = 50)
    private String sensorType;

    @JsonProperty("xCoordinate")
    @NotNull(message = "X 좌표(xCoordinate)를 입력하세요.")
    private Double xCoordinate;

    @JsonProperty("yCoordinate")
    @NotNull(message = "Y 좌표(yCoordinate)를 입력하세요.")
    private Double yCoordinate;

    @Size(max = 500)
    private String description;
}