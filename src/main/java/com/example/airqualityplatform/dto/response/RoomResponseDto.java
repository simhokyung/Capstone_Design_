package com.example.airqualityplatform.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class RoomResponseDto {
    private Long roomId;
    private String roomName;
    private String polygon;
    private Integer floorNumber;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}