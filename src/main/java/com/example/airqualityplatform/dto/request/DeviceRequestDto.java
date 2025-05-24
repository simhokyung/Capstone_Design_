package com.example.airqualityplatform.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeviceRequestDto {

    @NotBlank(message = "외부 식별자(deviceId)를 입력하세요.")
    private String deviceId;

    @NotNull(message = "Room ID를 입력하세요.")
    private Long roomId;

    @NotBlank(message = "장치 이름(deviceName)을 입력하세요.")
    private String deviceName;

    @NotBlank(message = "장치 타입(deviceType)을 입력하세요.")
    private String deviceType;

    @JsonProperty("xCoordinate")
    @NotNull(message = "X 좌표를 입력하세요.")
    private Double xCoordinate;

    @JsonProperty("yCoordinate")
    @NotNull(message = "Y 좌표를 입력하세요.")
    private Double yCoordinate;

    @NotNull(message = "전원 상태(power)를 입력하세요.")
    private Boolean power;

    @NotBlank(message = "팬 모드(fanMode)를 입력하세요.")
    private String fanMode;
}