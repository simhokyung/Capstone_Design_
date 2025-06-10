package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Set;

@Getter @Setter
public class ScheduleRequestDto {

    @NotBlank(message = "외부 기기 ID를 입력하세요.")
    private String deviceId;

    private Long userId;

    @NotNull(message = "시작 시간을 입력하세요.")
    private LocalTime startTime;

    @NotNull(message = "종료 시간을 입력하세요.")
    private LocalTime endTime;

    @NotEmpty(message = "요일 정보를 하나 이상 입력하세요.")
    private Set<DayOfWeek> daysOfWeek;
}