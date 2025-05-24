package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.DeviceStatus;
import com.example.airqualityplatform.dto.response.DeviceStatusResponseDto;

public class DeviceStatusResponseMapper {

    public static DeviceStatusResponseDto toDto(DeviceStatus s) {
        DeviceStatusResponseDto dto = new DeviceStatusResponseDto();
        dto.setId(s.getId());
        dto.setDeviceId(s.getDevice().getDeviceId());
        dto.setStatusAt(s.getStatusAt());
        dto.setEnergy(s.getEnergy());
        dto.setPowerState(s.getPowerState());
        dto.setFanMode(s.getFanMode());
        dto.setFetchedAt(s.getFetchedAt());
        return dto;
    }
}
