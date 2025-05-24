package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Room;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.mapper.SensorMapper;
import com.example.airqualityplatform.dto.request.SensorRequestDto;
import com.example.airqualityplatform.dto.response.SensorResponseDto;
import com.example.airqualityplatform.repository.RoomRepository;
import com.example.airqualityplatform.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SensorServiceTest {

    @InjectMocks
    private SensorService sensorService;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private RoomRepository roomRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateSensor() {
        SensorRequestDto req = new SensorRequestDto();
        req.setRoomId(1L);
        req.setDeviceId(2L);
        req.setSensorName("Test Sensor");
        req.setSensorType("AirQuality");
        req.setXCoordinate(10.0);
        req.setYCoordinate(20.0);
        req.setDescription("Test Description");

        Room room = new Room();
        room.setRoomId(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        Sensor sensorEntity = SensorMapper.toEntity(req, null);
        sensorEntity.setSensorId(50L);
        Date now = new Date();
        sensorEntity.setRegisteredAt(now);
        sensorEntity.setUpdatedAt(now);
        sensorEntity.setRoom(room);

        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensorEntity);

        SensorResponseDto response = sensorService.createSensor(req);
        assertNotNull(response);
        assertEquals(50L, response.getSensorId());
        assertEquals("Test Sensor", response.getSensorName());
        verify(roomRepository, times(1)).findById(1L);
        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    public void testGetAllSensors() {
        Sensor sensor1 = new Sensor();
        sensor1.setSensorId(50L);
        sensor1.setSensorName("Sensor1");

        Sensor sensor2 = new Sensor();
        sensor2.setSensorId(51L);
        sensor2.setSensorName("Sensor2");

        when(sensorRepository.findAll()).thenReturn(Arrays.asList(sensor1, sensor2));
        assertEquals(2, sensorService.getAllSensors().size());
        verify(sensorRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateSensor() {
        Long sensorId = 50L;
        Sensor existing = new Sensor();
        existing.setSensorId(sensorId);
        existing.setSensorName("Old Sensor");
        existing.setSensorType("AirQuality");
        existing.setXCoordinate(10.0);
        existing.setYCoordinate(20.0);
        existing.setDescription("Old Description");

        SensorRequestDto req = new SensorRequestDto();
        req.setRoomId(1L);
        req.setDeviceId(2L);
        req.setSensorName("Updated Sensor");
        req.setSensorType("Temperature");
        req.setXCoordinate(15.0);
        req.setYCoordinate(25.0);
        req.setDescription("Updated Description");

        Room room = new Room();
        room.setRoomId(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(existing));
        when(sensorRepository.save(any(Sensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SensorResponseDto response = sensorService.updateSensor(sensorId, req);
        assertNotNull(response);
        assertEquals("Updated Sensor", response.getSensorName());
        assertEquals("Temperature", response.getSensorType());
        assertEquals(15.0, response.getXCoordinate());
        assertEquals(25.0, response.getYCoordinate());
        assertEquals("Updated Description", response.getDescription());
        verify(sensorRepository, times(1)).findById(sensorId);
        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    public void testDeleteSensor() {
        Long sensorId = 50L;
        doNothing().when(sensorRepository).deleteById(sensorId);
        sensorService.deleteSensor(sensorId);
        verify(sensorRepository, times(1)).deleteById(sensorId);
    }
}
