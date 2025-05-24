package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.mapper.MeasurementMapper;
import com.example.airqualityplatform.dto.request.MeasurementRequestDto;
import com.example.airqualityplatform.dto.response.MeasurementResponseDto;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class MeasurementMapperTest {

    @Test
    public void testToEntity() {
        // MeasurementRequestDto 객체 생성
        MeasurementRequestDto req = new MeasurementRequestDto();
        req.setSensorId(10L);
        Date now = new Date();
        req.setTimestamp(now);
        req.setTemperature(23.5);
        req.setHumidity(45.0);
        req.setCo2(800.0);
        req.setVoc(0.12);
        req.setPm25(55.0);
        req.setPm10(70.0);
        req.setFanSpeed(2);

        // DTO -> Measurement 엔티티 변환 (업데이트 대상은 null)
        Measurement measurement = MeasurementMapper.toEntity(req, null);

        assertNotNull(measurement);
        assertEquals(now, measurement.getTimestamp());
        assertEquals(23.5, measurement.getTemperature());
        assertEquals(45.0, measurement.getHumidity());
        assertEquals(800.0, measurement.getCo2());
        assertEquals(0.12, measurement.getVoc());
        assertEquals(55.0, measurement.getPm25());
        assertEquals(70.0, measurement.getPm10());
        assertEquals(2, measurement.getFanSpeed());
        // Sensor는 Service 계층에서 별도로 설정하므로 여기서는 null일 수 있음
    }

    @Test
    public void testToResponseDto() {
        // Measurement 엔티티 생성 및 Sensor 설정
        Measurement m = new Measurement();
        m.setMeasurementId(50L);
        Sensor sensor = new Sensor();
        sensor.setSensorId(20L);
        m.setSensor(sensor);
        Date now = new Date();
        m.setTimestamp(now);
        m.setTemperature(22.0);
        m.setHumidity(40.0);
        m.setCo2(750.0);
        m.setVoc(0.1);
        m.setPm25(50.0);
        m.setPm10(65.0);
        m.setFanSpeed(1);

        MeasurementResponseDto dto = MeasurementMapper.toResponseDto(m);
        assertNotNull(dto);
        assertEquals(50L, dto.getMeasurementId());
        assertEquals(20L, dto.getSensorId());
        assertEquals(now, dto.getTimestamp());
        assertEquals(22.0, dto.getTemperature());
        assertEquals(40.0, dto.getHumidity());
        assertEquals(750.0, dto.getCo2());
        assertEquals(0.1, dto.getVoc());
        assertEquals(50.0, dto.getPm25());
        assertEquals(65.0, dto.getPm10());
        assertEquals(1, dto.getFanSpeed());
    }
}
