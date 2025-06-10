package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.Schedule;
import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.mapper.ScheduleMapper;
import com.example.airqualityplatform.dto.request.ScheduleRequestDto;
import com.example.airqualityplatform.dto.response.ScheduleResponseDto;
import com.example.airqualityplatform.repository.ScheduleRepository;
import com.example.airqualityplatform.repository.DeviceRepository;
import com.example.airqualityplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateSchedule() {
        ScheduleRequestDto req = new ScheduleRequestDto();
        req.setDeviceId(20L);
        req.setUserId(15L);
        req.setStartTime("07:00");
        req.setEndTime("09:00");
        req.setDaysOfWeek("MON,TUE,WED");

        // 모킹: Device 및 User 조회
        Device device = new Device();
        device.setDeviceId(20L);
        User user = new User();
        user.setUserId(15L);
        when(deviceRepository.findById(20L)).thenReturn(Optional.of(device));
        when(userRepository.findById(15L)).thenReturn(Optional.of(user));

        // DTO -> 엔티티 변환 및 Repository 저장 모킹
        Schedule schedule = ScheduleMapper.toEntity(req, null);
        schedule.setDevice(device);
        schedule.setUser(user);
        schedule.setScheduleId(30L);
        Date now = new Date();
        schedule.setCreatedAt(now);
        schedule.setUpdatedAt(now);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        ScheduleResponseDto response = scheduleService.createSchedule(req);
        assertNotNull(response);
        assertEquals(30L, response.getScheduleId());
        assertEquals(20L, response.getDeviceId());
        assertEquals(15L, response.getUserId());
        assertEquals("07:00", response.getStartTime());
        assertEquals("09:00", response.getEndTime());
        assertEquals("MON,TUE,WED", response.getDaysOfWeek());
        verify(deviceRepository, times(1)).findById(20L);
        verify(userRepository, times(1)).findById(15L);
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    public void testGetAllSchedules() {
        Schedule s1 = new Schedule();
        s1.setScheduleId(30L);
        s1.setStartTime("07:00");
        s1.setEndTime("09:00");
        s1.setDaysOfWeek("MON,TUE,WED");
        s1.setCreatedAt(new Date());
        s1.setUpdatedAt(new Date());
        // **중요:** 각 Schedule에는 Device가 세팅되어야 함 (오류 발생 방지)
        Device d = new Device();
        d.setDeviceId(20L);
        s1.setDevice(d);

        Schedule s2 = new Schedule();
        s2.setScheduleId(31L);
        s2.setStartTime("10:00");
        s2.setEndTime("12:00");
        s2.setDaysOfWeek("THU,FRI");
        s2.setCreatedAt(new Date());
        s2.setUpdatedAt(new Date());
        Device d2 = new Device();
        d2.setDeviceId(21L);
        s2.setDevice(d2);

        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(s1, s2));
        assertEquals(2, scheduleService.getAllSchedules().size());
        verify(scheduleRepository, times(1)).findAll();
    }

    @Test
    public void testGetScheduleById() {
        Schedule s = new Schedule();
        s.setScheduleId(30L);
        s.setStartTime("07:00");
        s.setEndTime("09:00");
        s.setDaysOfWeek("MON,TUE,WED");
        s.setCreatedAt(new Date());
        s.setUpdatedAt(new Date());
        Device d = new Device();
        d.setDeviceId(20L);
        s.setDevice(d);
        User u = new User();
        u.setUserId(15L);
        s.setUser(u);

        when(scheduleRepository.findById(30L)).thenReturn(Optional.of(s));
        ScheduleResponseDto result = scheduleService.getScheduleById(30L);
        assertNotNull(result);
        assertEquals("07:00", result.getStartTime());

        verify(scheduleRepository, times(1)).findById(30L);
    }

    @Test
    public void testUpdateSchedule() {
        Long scheduleId = 30L;
        Schedule existing = new Schedule();
        existing.setScheduleId(scheduleId);
        existing.setStartTime("07:00");
        existing.setEndTime("09:00");
        existing.setDaysOfWeek("MON,TUE,WED");

        ScheduleRequestDto req = new ScheduleRequestDto();
        req.setDeviceId(20L);
        req.setUserId(15L);
        req.setStartTime("08:00");
        req.setEndTime("10:00");
        req.setDaysOfWeek("MON,TUE,WED,THU");

        // 모킹: Device와 User 조회
        Device device = new Device();
        device.setDeviceId(20L);
        when(deviceRepository.findById(20L)).thenReturn(Optional.of(device));
        User user = new User();
        user.setUserId(15L);
        when(userRepository.findById(15L)).thenReturn(Optional.of(user));

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existing));
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ScheduleResponseDto response = scheduleService.updateSchedule(scheduleId, req);
        assertNotNull(response);
        assertEquals("08:00", response.getStartTime());
        assertEquals("10:00", response.getEndTime());
        assertEquals("MON,TUE,WED,THU", response.getDaysOfWeek());
        verify(scheduleRepository, times(1)).findById(scheduleId);
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    public void testDeleteSchedule() {
        Long scheduleId = 30L;
        doNothing().when(scheduleRepository).deleteById(scheduleId);
        scheduleService.deleteSchedule(scheduleId);
        verify(scheduleRepository, times(1)).deleteById(scheduleId);
    }
}
