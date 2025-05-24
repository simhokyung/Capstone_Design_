package com.example.airqualityplatform.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Set;

@Getter @Setter
public class ScheduleResponseDto {
    private Long scheduleId;
    private String deviceId;
    private Long userId;
    private LocalTime startTime;
    private LocalTime endTime;
    private Set<DayOfWeek> daysOfWeek;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}