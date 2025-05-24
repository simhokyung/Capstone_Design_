package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.Schedule;
import com.example.airqualityplatform.dto.request.ScheduleRequestDto;
import com.example.airqualityplatform.dto.response.ScheduleResponseDto;

public class ScheduleMapper {

    public static ScheduleResponseDto toResponseDto(Schedule schedule) {
        ScheduleResponseDto dto = new ScheduleResponseDto();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setDeviceId(schedule.getDevice().getDeviceId());
        dto.setUserId(
                schedule.getUser() != null ? schedule.getUser().getUserId() : null
        );
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setDaysOfWeek(schedule.getDaysOfWeek());
        dto.setCreatedAt(schedule.getCreatedAt());
        dto.setUpdatedAt(schedule.getUpdatedAt());
        return dto;
    }

    public static Schedule toEntity(ScheduleRequestDto dto, Schedule schedule) {
        if (schedule == null) {
            schedule = new Schedule();
        }
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setDaysOfWeek(dto.getDaysOfWeek());
        return schedule;
    }
}