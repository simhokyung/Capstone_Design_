// src/main/java/com/example/airqualityplatform/dto/request/FilterRequestDto.java
package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FilterRequestDto {

    @NotBlank(message = "filterName을 입력하세요.")
    @Size(max = 50, message = "filterName은 최대 50자까지 가능합니다.")
    private String filterName;

    @NotNull(message = "deviceId를 입력하세요.")
    private String deviceId;

    @NotNull(message = "sensorId를 입력하세요.")
    private Long sensorId;
}
