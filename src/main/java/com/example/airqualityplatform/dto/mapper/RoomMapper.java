package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.Room;
import com.example.airqualityplatform.dto.request.RoomRequestDto;
import com.example.airqualityplatform.dto.response.RoomResponseDto;

public class RoomMapper {

    public static Room toEntity(RoomRequestDto dto, Room room) {
        if (room == null) {
            room = new Room();
        }
        room.setRoomName(dto.getRoomName());
        room.setPolygon(dto.getPolygon());
        room.setFloorNumber(dto.getFloorNumber());
        room.setDescription(dto.getDescription());
        return room;
    }

    public static RoomResponseDto toResponseDto(Room room) {
        RoomResponseDto dto = new RoomResponseDto();
        dto.setRoomId(room.getRoomId());
        dto.setRoomName(room.getRoomName());
        dto.setPolygon(room.getPolygon());
        dto.setFloorNumber(room.getFloorNumber());
        dto.setDescription(room.getDescription());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());
        return dto;
    }
}