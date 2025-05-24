package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.mapper.SensorMapper;
import com.example.airqualityplatform.dto.request.SensorRequestDto;
import com.example.airqualityplatform.dto.response.SensorResponseDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SensorMapperTest {

    @Test
    public void testToEntity() {
        // SensorRequestDto 객체 생성 및 값 설정
        SensorRequestDto req = new SensorRequestDto();
        req.setRoomId(1L);
        req.setDeviceId(2L);
        req.setSensorName("Test Sensor");
        req.setSensorType("AirQuality");
        req.setXCoordinate(10.0);
        req.setYCoordinate(20.0);
        req.setDescription("Test Description");

        // DTO → Sensor 엔티티 변환 (업데이트 대상은 null)
        Sensor sensor = SensorMapper.toEntity(req, null);

        assertNotNull(sensor);
        assertEquals("Test Sensor", sensor.getSensorName());
        assertEquals("AirQuality", sensor.getSensorType());
        assertEquals(10.0, sensor.getXCoordinate());
        assertEquals(20.0, sensor.getYCoordinate());
        assertEquals("Test Description", sensor.getDescription());
        // room, device 연관관계는 Service 계층에서 설정하므로 여기서는 null일 수 있음
    }

    @Test
    public void testToResponseDto() {
        // Sensor 엔티티 생성 및 값 설정
        Sensor sensor = new Sensor();
        sensor.setSensorId(100L);
        sensor.setSensorName("Sensor 100");
        sensor.setSensorType("Temperature");
        sensor.setXCoordinate(15.0);
        sensor.setYCoordinate(25.0);
        sensor.setDescription("Room sensor");
        java.util.Date now = new java.util.Date();
        sensor.setRegisteredAt(now);
        sensor.setUpdatedAt(now);

        SensorResponseDto dto = SensorMapper.toResponseDto(sensor);

        assertNotNull(dto);
        assertEquals(100L, dto.getSensorId());
        assertEquals("Sensor 100", dto.getSensorName());
        assertEquals("Temperature", dto.getSensorType());
        assertEquals(15.0, dto.getXCoordinate());
        assertEquals(25.0, dto.getYCoordinate());
        assertEquals("Room sensor", dto.getDescription());
        assertEquals(now, dto.getRegisteredAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
