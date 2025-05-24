package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.DeviceAutoControl;
import com.example.airqualityplatform.dto.mapper.DeviceAutoControlMapper;
import com.example.airqualityplatform.dto.request.DeviceAutoControlRequestDto;
import com.example.airqualityplatform.dto.response.DeviceAutoControlResponseDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class DeviceAutoControlMapperTest {

    @Test
    public void testToEntity() {
        // DeviceAutoControlRequestDto 생성 및 값 설정
        DeviceAutoControlRequestDto req = new DeviceAutoControlRequestDto();
        req.setPm25Threshold(40.0);
        req.setCo2Threshold(900.0);
        req.setActionPower(true);
        req.setActionMode("auto");
        req.setActionFanSpeed(3);

        // DTO를 엔티티로 변환 (업데이트 대상은 null)
        DeviceAutoControl dac = DeviceAutoControlMapper.toEntity(req, null);
        assertNotNull(dac);
        assertEquals(40.0, dac.getPm25Threshold());
        assertEquals(900.0, dac.getCo2Threshold());
        assertTrue(dac.getActionPower());
        assertEquals("auto", dac.getActionMode());
        assertEquals(3, dac.getActionFanSpeed());
    }

    @Test
    public void testToResponseDto() {
        // DeviceAutoControl 엔티티 생성 및 값 설정
        DeviceAutoControl dac = new DeviceAutoControl();
        dac.setControlId(123L);
        dac.setPm25Threshold(40.0);
        dac.setCo2Threshold(900.0);
        dac.setActionPower(true);
        dac.setActionMode("auto");
        dac.setActionFanSpeed(3);
        Date now = new Date();
        dac.setCreatedAt(now);
        dac.setUpdatedAt(now);

        DeviceAutoControlResponseDto dto = DeviceAutoControlMapper.toResponseDto(dac);
        assertNotNull(dto);
        assertEquals(123L, dto.getControlId());
        assertEquals(40.0, dto.getPm25Threshold());
        assertEquals(900.0, dto.getCo2Threshold());
        assertTrue(dto.getActionPower());
        assertEquals("auto", dto.getActionMode());
        assertEquals(3, dto.getActionFanSpeed());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
