package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.Room;
import com.example.airqualityplatform.dto.mapper.DeviceMapper;
import com.example.airqualityplatform.dto.request.DeviceRequestDto;
import com.example.airqualityplatform.dto.response.DeviceResponseDto;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class DeviceMapperTest {

    @Test
    public void testToEntity() {
        // DeviceRequestDto 객체 생성 및 필드 설정
        DeviceRequestDto req = new DeviceRequestDto();
        // roomId는 Service 계층에서 사용하므로 여기선 포함해도 되지만, Mapper는 room 객체를 별도로 받아 처리
        req.setDeviceName("공기청정기 A");
        req.setDeviceType("air_purifier");
        req.setXCoordinate(50.0);
        req.setYCoordinate(75.0);
        req.setPower(true);
        req.setMode("auto");
        req.setFanSpeed(2);

        // Room 객체 생성 (예: ID 1)
        Room room = new Room();
        room.setRoomId(1L);

        // DTO -> Device 엔티티 변환 (업데이트 대상은 null)
        Device device = DeviceMapper.toEntity(req, null, room);
        assertNotNull(device);
        assertEquals("공기청정기 A", device.getDeviceName());
        assertEquals("air_purifier", device.getDeviceType());
        assertEquals(50.0, device.getXCoordinate());
        assertEquals(75.0, device.getYCoordinate());
        assertTrue(device.getPower());
        assertEquals("auto", device.getMode());
        assertEquals(2, device.getFanSpeed());
        assertEquals(room, device.getRoom());
    }

    @Test
    public void testToResponseDto() {
        // Device 엔티티 생성 및 필드 설정
        Device device = new Device();
        device.setDeviceId(100L);
        device.setDeviceName("공기청정기 A");
        device.setDeviceType("air_purifier");
        device.setXCoordinate(50.0);
        device.setYCoordinate(75.0);
        device.setPower(true);
        device.setMode("auto");
        device.setFanSpeed(2);
        Date now = new Date();
        device.setRegisteredAt(now);
        device.setUpdatedAt(now);

        DeviceResponseDto dto = DeviceMapper.toResponseDto(device);

        assertNotNull(dto);
        assertEquals(100L, dto.getDeviceId());
        assertEquals("공기청정기 A", dto.getDeviceName());
        assertEquals("air_purifier", dto.getDeviceType());
        assertEquals(50.0, dto.getXCoordinate());
        assertEquals(75.0, dto.getYCoordinate());
        assertTrue(dto.getPower());
        assertEquals("auto", dto.getMode());
        assertEquals(2, dto.getFanSpeed());
        assertEquals(now, dto.getRegisteredAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
