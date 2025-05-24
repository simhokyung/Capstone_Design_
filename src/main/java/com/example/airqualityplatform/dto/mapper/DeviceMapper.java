package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.Room;
import com.example.airqualityplatform.dto.request.DeviceRequestDto;
import com.example.airqualityplatform.dto.response.DeviceResponseDto;

public class DeviceMapper {

    public static Device toEntity(DeviceRequestDto dto, Device device, Room room) {
        if (device == null) {
            device = new Device();
        }
        device.setDeviceId(dto.getDeviceId());
        device.setRoom(room);
        device.setDeviceName(dto.getDeviceName());
        device.setDeviceType(dto.getDeviceType());
        device.setXCoordinate(dto.getXCoordinate());
        device.setYCoordinate(dto.getYCoordinate());
        device.setPower(dto.getPower());
        device.setFanMode(dto.getFanMode());
        return device;
    }

    public static DeviceResponseDto toResponseDto(Device device) {
        DeviceResponseDto dto = new DeviceResponseDto();
        dto.setId(device.getId());
        dto.setDeviceId(device.getDeviceId());
        dto.setDeviceName(device.getDeviceName());
        dto.setDeviceType(device.getDeviceType());
        dto.setXCoordinate(device.getXCoordinate());
        dto.setYCoordinate(device.getYCoordinate());
        dto.setPower(device.getPower());
        dto.setFanMode(device.getFanMode());
        dto.setRegisteredAt(device.getRegisteredAt());
        dto.setUpdatedAt(device.getUpdatedAt());
        dto.setRoomId(device.getRoom().getRoomId());
        dto.setPolicyId(device.getPolicy() != null ? device.getPolicy().getControlId() : null);
        return dto;
    }
}