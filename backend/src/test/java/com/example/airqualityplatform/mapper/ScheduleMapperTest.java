package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.Schedule;
import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.mapper.ScheduleMapper;
import com.example.airqualityplatform.dto.request.ScheduleRequestDto;
import com.example.airqualityplatform.dto.response.ScheduleResponseDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class ScheduleMapperTest {

    @Test
    public void testToEntity() {
        // ScheduleRequestDto 객체 생성 및 값 설정
        ScheduleRequestDto req = new ScheduleRequestDto();
        req.setDeviceId(20L);
        req.setUserId(15L);
        req.setStartTime("08:00");
        req.setEndTime("10:00");
        req.setDaysOfWeek("MON,TUE,WED");

        // DTO -> Schedule 엔티티 변환 (업데이트 대상은 null)
        Schedule schedule = ScheduleMapper.toEntity(req, null);
        assertNotNull(schedule);
        assertEquals("08:00", schedule.getStartTime());
        assertEquals("10:00", schedule.getEndTime());
        assertEquals("MON,TUE,WED", schedule.getDaysOfWeek());
        // Device, User 등 연관관계는 Service 계층에서 설정하므로 여기서는 미반영됨.
    }

    @Test
    public void testToResponseDto() {
        // Schedule 엔티티 생성 및 값 설정
        Schedule schedule = new Schedule();
        schedule.setScheduleId(30L);
        // Device와 User 객체 생성 후 설정 (ID만 확인)
        Device device = new Device();
        device.setDeviceId(20L);
        schedule.setDevice(device);
        User user = new User();
        user.setUserId(15L);
        schedule.setUser(user);

        schedule.setStartTime("08:00");
        schedule.setEndTime("10:00");
        schedule.setDaysOfWeek("MON,TUE,WED");
        Date now = new Date();
        schedule.setCreatedAt(now);
        schedule.setUpdatedAt(now);

        ScheduleResponseDto dto = ScheduleMapper.toResponseDto(schedule);
        assertNotNull(dto);
        assertEquals(30L, dto.getScheduleId());
        assertEquals(20L, dto.getDeviceId());
        assertEquals(15L, dto.getUserId());
        assertEquals("08:00", dto.getStartTime());
        assertEquals("10:00", dto.getEndTime());
        assertEquals("MON,TUE,WED", dto.getDaysOfWeek());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
