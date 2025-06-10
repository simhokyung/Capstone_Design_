package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.Room;
import com.example.airqualityplatform.dto.mapper.DeviceMapper;
import com.example.airqualityplatform.dto.request.DeviceRequestDto;
import com.example.airqualityplatform.dto.response.DeviceResponseDto;
import com.example.airqualityplatform.repository.DeviceRepository;
import com.example.airqualityplatform.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeviceServiceTest {

    @InjectMocks
    private DeviceService deviceService;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private RoomRepository roomRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Device 생성 테스트
    @Test
    public void testCreateDevice() {
        DeviceRequestDto req = new DeviceRequestDto();
        req.setRoomId(1L);
        req.setDeviceName("Air Purifier");
        req.setDeviceType("air_purifier");
        req.setXCoordinate(50.0);
        req.setYCoordinate(75.0);
        req.setPower(true);
        req.setMode("auto");
        req.setFanSpeed(2);

        Room room = new Room();
        room.setRoomId(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        Device deviceEntity = DeviceMapper.toEntity(req, null, room);
        deviceEntity.setDeviceId(100L);
        Date now = new Date();
        deviceEntity.setRegisteredAt(now);
        deviceEntity.setUpdatedAt(now);

        when(deviceRepository.save(any(Device.class))).thenReturn(deviceEntity);

        DeviceResponseDto response = deviceService.createDevice(req);
        assertNotNull(response);
        assertEquals(100L, response.getDeviceId());
        assertEquals("Air Purifier", response.getDeviceName());
        verify(roomRepository, times(1)).findById(1L);
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    // 전체 Device 조회 테스트
    @Test
    public void testGetAllDevices() {
        Device d1 = new Device();
        d1.setDeviceId(100L);
        d1.setDeviceName("Device1");
        Device d2 = new Device();
        d2.setDeviceId(101L);
        d2.setDeviceName("Device2");

        when(deviceRepository.findAll()).thenReturn(Arrays.asList(d1, d2));
        assertEquals(2, deviceService.getAllDevices().size());
        verify(deviceRepository, times(1)).findAll();
    }

    // Device 수정 테스트
    @Test
    public void testUpdateDevice() {
        Long deviceId = 100L;
        Device existing = new Device();
        existing.setDeviceId(deviceId);
        existing.setDeviceName("Old Device");
        existing.setDeviceType("air_purifier");
        existing.setXCoordinate(10.0);
        existing.setYCoordinate(20.0);
        existing.setPower(false);
        existing.setMode("manual");
        existing.setFanSpeed(1);

        DeviceRequestDto req = new DeviceRequestDto();
        req.setRoomId(1L);
        req.setDeviceName("Updated Device");
        req.setDeviceType("air_purifier");
        req.setXCoordinate(15.0);
        req.setYCoordinate(25.0);
        req.setPower(true);
        req.setMode("auto");
        req.setFanSpeed(2);

        Room room = new Room();
        room.setRoomId(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(existing));
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeviceResponseDto response = deviceService.updateDevice(deviceId, req);
        assertNotNull(response);
        assertEquals("Updated Device", response.getDeviceName());
        assertEquals(15.0, response.getXCoordinate());
        assertEquals("auto", response.getMode());
        verify(deviceRepository, times(1)).findById(deviceId);
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    // Device 삭제 테스트
    @Test
    public void testDeleteDevice() {
        Long deviceId = 100L;
        doNothing().when(deviceRepository).deleteById(deviceId);
        deviceService.deleteDevice(deviceId);
        verify(deviceRepository, times(1)).deleteById(deviceId);
    }
}
