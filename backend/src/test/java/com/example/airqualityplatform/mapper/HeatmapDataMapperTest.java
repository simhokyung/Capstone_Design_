package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.mapper.HeatmapDataMapper;
import com.example.airqualityplatform.dto.response.HeatmapDataResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class HeatmapDataMapperTest {

    @Test
    public void testToDto() {
        // Sensor 생성 및 필드 설정
        Sensor sensor = new Sensor();
        sensor.setSensorId(101L);
        sensor.setXCoordinate(55.0);
        sensor.setYCoordinate(75.0);

        // Measurement 생성 및 필드 설정
        Measurement measurement = new Measurement();
        measurement.setPm25(60.0);
        Date now = new Date();
        measurement.setTimestamp(now);

        // HeatmapDataMapper를 사용하여 DTO 변환
        HeatmapDataResponse dto = HeatmapDataMapper.toDto(sensor, measurement);

        assertNotNull(dto);
        assertEquals(101L, dto.getSensorId());
        assertEquals(55.0, dto.getXCoordinate());
        assertEquals(75.0, dto.getYCoordinate());
        assertEquals(60.0, dto.getPm25());
        assertEquals(now, dto.getTimestamp());
    }
}
