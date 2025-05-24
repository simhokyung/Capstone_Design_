package com.example.airqualityplatform.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class SensorResponseDto {
    private Long id;
    private Long sensorId;
    private Long roomId;
    private String sensorName;
    private String sensorType;
    private Double xCoordinate;
    private Double yCoordinate;
    private String description;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
}