// src/main/java/com/example/airqualityplatform/dto/response/FilterResponseDto.java
package com.example.airqualityplatform.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class FilterResponseDto {
    private Long filterId;
    private String filterName;
    private String deviceId;
    private Long sensorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
