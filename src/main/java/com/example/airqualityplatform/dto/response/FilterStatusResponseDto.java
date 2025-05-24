package com.example.airqualityplatform.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter @Setter
public class FilterStatusResponseDto {
    private Long id;
    private Long filterId;
    private Double capacity;
    private String capacityUnit;
    private Double usedHours;
    private Double usedHoursStep;
    private String status;
    private List<String> resetTypes;
    private Instant lastResetDate;
    private Instant fetchedAt;
    private Instant statusAt;
}