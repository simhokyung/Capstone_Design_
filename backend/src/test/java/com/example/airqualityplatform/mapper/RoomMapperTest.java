package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.Room;
import com.example.airqualityplatform.dto.mapper.RoomMapper;
import com.example.airqualityplatform.dto.request.RoomRequestDto;
import com.example.airqualityplatform.dto.response.RoomResponseDto;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class RoomMapperTest {

    @Test
    public void testToEntity() {
        // RoomRequestDto 생성 및 필드 설정
        RoomRequestDto req = new RoomRequestDto();
        req.setRoomName("거실");
        req.setPolygon("[[0,0],[0,10],[10,10],[10,0],[0,0]]");
        req.setFloorNumber(1);
        req.setDescription("넓은 거실");

        // DTO -> Room 엔티티 변환
        Room room = RoomMapper.toEntity(req, null);

        assertNotNull(room);
        assertEquals("거실", room.getRoomName());
        assertEquals("[[0,0],[0,10],[10,10],[10,0],[0,0]]", room.getPolygon());
        assertEquals(1, room.getFloorNumber());
        assertEquals("넓은 거실", room.getDescription());
    }

    @Test
    public void testToResponseDto() {
        // Room 엔티티 생성 및 필드 설정
        Room room = new Room();
        room.setRoomId(1L);
        room.setRoomName("거실");
        room.setPolygon("[[0,0],[0,10],[10,10],[10,0],[0,0]]");
        room.setFloorNumber(1);
        room.setDescription("넓은 거실");
        Date now = new Date();
        room.setCreatedAt(now);
        room.setUpdatedAt(now);

        RoomResponseDto dto = RoomMapper.toResponseDto(room);

        assertNotNull(dto);
        assertEquals(1L, dto.getRoomId());
        assertEquals("거실", dto.getRoomName());
        assertEquals("[[0,0],[0,10],[10,10],[10,0],[0,0]]", dto.getPolygon());
        assertEquals(1, dto.getFloorNumber());
        assertEquals("넓은 거실", dto.getDescription());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
