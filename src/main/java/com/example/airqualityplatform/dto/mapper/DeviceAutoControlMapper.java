package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.DeviceAutoControl;
import com.example.airqualityplatform.dto.request.DeviceAutoControlRequestDto;
import com.example.airqualityplatform.dto.response.DeviceAutoControlResponseDto;
import com.example.airqualityplatform.domain.Device;

import java.util.stream.Collectors;

public class DeviceAutoControlMapper {

    public static DeviceAutoControl toEntity(DeviceAutoControlRequestDto dto, DeviceAutoControl entity) {
        if (entity == null) {
            entity = new DeviceAutoControl();
        }
        entity.setPm25Threshold(dto.getPm25Threshold());
        entity.setCo2Threshold(dto.getCo2Threshold());
        entity.setPm100Threshold(dto.getPm100Threshold());
        entity.setVocThreshold(dto.getVocThreshold());
        entity.setActionPower(dto.getActionPower());
        entity.setActionMode(dto.getActionMode());
        return entity;
    }

    public static DeviceAutoControlResponseDto toResponseDto(DeviceAutoControl entity) {
        DeviceAutoControlResponseDto dto = new DeviceAutoControlResponseDto();
        dto.setControlId(entity.getControlId());
        dto.setRoomId(entity.getDevices().isEmpty() ? null : entity.getDevices().get(0).getRoom().getRoomId());
        dto.setDeviceIds(entity.getDevices().stream()
                .map(Device::getId)
                .collect(Collectors.toList()));
        dto.setPm25Threshold(entity.getPm25Threshold());
        dto.setCo2Threshold(entity.getCo2Threshold());
        dto.setPm100Threshold(entity.getPm100Threshold());
        dto.setVocThreshold(entity.getVocThreshold());
        dto.setActionPower(entity.getActionPower());
        dto.setActionMode(entity.getActionMode());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}