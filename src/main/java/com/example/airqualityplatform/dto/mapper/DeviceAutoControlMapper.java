package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.DeviceAutoControl;
import com.example.airqualityplatform.dto.request.AiPolicyRequestDto;
import com.example.airqualityplatform.dto.request.DeviceAutoControlRequestDto;
import com.example.airqualityplatform.dto.response.DeviceAutoControlResponseDto;

import java.util.stream.Collectors;

public class DeviceAutoControlMapper {

    public static DeviceAutoControl toEntity(DeviceAutoControlRequestDto dto,
                                             DeviceAutoControl entity) {
        if (entity == null) entity = new DeviceAutoControl();
        entity.setPm25Threshold(dto.getPm25Threshold());
        entity.setCo2Threshold(dto.getCo2Threshold());
        entity.setPm100Threshold(dto.getPm100Threshold());
        entity.setVocThreshold(dto.getVocThreshold());
        return entity;
    }

    public static DeviceAutoControlResponseDto toResponseDto(DeviceAutoControl e) {
        DeviceAutoControlResponseDto dto = new DeviceAutoControlResponseDto();
        dto.setControlId(e.getControlId());
        dto.setRoomId(
                e.getDevices().isEmpty()
                        ? null
                        : e.getDevices().get(0).getRoom().getRoomId());
        dto.setDeviceIds(
                e.getDevices().stream()
                        .map(Device::getId)
                        .collect(Collectors.toList()));
        dto.setPm25Threshold(e.getPm25Threshold());
        dto.setCo2Threshold(e.getCo2Threshold());
        dto.setPm100Threshold(e.getPm100Threshold());
        dto.setVocThreshold(e.getVocThreshold());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    public static AiPolicyRequestDto toAiDto(DeviceAutoControl e) {
        AiPolicyRequestDto dto = new AiPolicyRequestDto();
        dto.setPolicyId(e.getControlId());
        dto.setRoomId(
                e.getDevices().isEmpty()
                        ? null
                        : e.getDevices().get(0).getRoom().getRoomId());

        AiPolicyRequestDto.Thresholds t = new AiPolicyRequestDto.Thresholds();
        t.setPm25(e.getPm25Threshold());
        t.setPm100(e.getPm100Threshold());
        t.setCo2(e.getCo2Threshold());
        t.setVoc(e.getVocThreshold());
        dto.setThresholds(t);

        dto.setDeviceIds(
                e.getDevices().stream()
                        .map(Device::getDeviceId)
                        .collect(Collectors.toList()));

        return dto;
    }
}
