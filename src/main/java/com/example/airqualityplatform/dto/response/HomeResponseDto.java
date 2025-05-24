package com.example.airqualityplatform.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class HomeResponseDto {
    private Long homeId;
    private String homeName;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}