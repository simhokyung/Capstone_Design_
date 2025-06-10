package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.request.SensorRequestDto;
import com.example.airqualityplatform.dto.response.SensorResponseDto;

public class SensorMapper {

    public static Sensor toEntity(SensorRequestDto dto, Sensor sensor) {
        if (sensor == null) sensor = new Sensor();
        sensor.setSensorId(dto.getSensorId());
        sensor.setSensorName(dto.getSensorName());
        sensor.setSensorType(dto.getSensorType());
        sensor.setXCoordinate(dto.getXCoordinate());
        sensor.setYCoordinate(dto.getYCoordinate());
        sensor.setDescription(dto.getDescription());
        return sensor;
    }

    public static SensorResponseDto toResponseDto(Sensor sensor) {
        SensorResponseDto dto = new SensorResponseDto();
        dto.setId(sensor.getId());
        dto.setSensorId(sensor.getSensorId());
        dto.setRoomId(sensor.getRoom().getRoomId());
        dto.setSensorName(sensor.getSensorName());
        dto.setSensorType(sensor.getSensorType());
        dto.setXCoordinate(sensor.getXCoordinate());
        dto.setYCoordinate(sensor.getYCoordinate());
        dto.setDescription(sensor.getDescription());
        dto.setRegisteredAt(sensor.getRegisteredAt());
        dto.setUpdatedAt(sensor.getUpdatedAt());
        return dto;
    }
}
