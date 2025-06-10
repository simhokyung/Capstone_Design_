package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.mapper.MeasurementMapper;
import com.example.airqualityplatform.dto.request.MeasurementRequestDto;
import com.example.airqualityplatform.dto.response.MeasurementResponseDto;
import com.example.airqualityplatform.repository.MeasurementRepository;
import com.example.airqualityplatform.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MeasurementServiceTest {

    @InjectMocks
    private MeasurementService measurementService;

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private SensorRepository sensorRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateMeasurement() {
        // 요청 DTO 생성
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

        // Sensor 조회 모킹: Sensor 객체를 생성하고 sensorId를 설정
        Sensor sensor = new Sensor();
        sensor.setSensorId(10L);
        when(sensorRepository.findById(10L)).thenReturn(Optional.of(sensor));

        // DTO -> 엔티티 변환
        Measurement measurement = MeasurementMapper.toEntity(req, null);
        // **필수!** Sensor 필드를 설정해야 Mapper가 null 에러가 나지 않음
        measurement.setSensor(sensor);
        measurement.setMeasurementId(50L);
        when(measurementRepository.save(any(Measurement.class))).thenReturn(measurement);

        MeasurementResponseDto response = measurementService.createMeasurement(req);
        assertNotNull(response);
        assertEquals(50L, response.getMeasurementId());
        assertEquals(10L, response.getSensorId());
        assertEquals(23.5, response.getTemperature());
        verify(sensorRepository, times(1)).findById(10L);
        verify(measurementRepository, times(1)).save(any(Measurement.class));
    }

    @Test
    public void testGetMeasurementById() {
        Measurement m = new Measurement();
        m.setMeasurementId(100L);
        Date now = new Date();
        m.setTimestamp(now);
        m.setTemperature(22.0);
        // **중요:** Sensor 객체를 반드시 설정해야 함
        Sensor sensor = new Sensor();
        sensor.setSensorId(10L);
        m.setSensor(sensor);

        when(measurementRepository.findById(100L)).thenReturn(Optional.of(m));

        MeasurementResponseDto response = measurementService.getMeasurementById(100L);
        assertNotNull(response);
        assertEquals(100L, response.getMeasurementId());
        assertEquals(10L, response.getSensorId());
        verify(measurementRepository, times(1)).findById(100L);
    }

    @Test
    public void testGetAllMeasurements() {
        Measurement m1 = new Measurement();
        m1.setMeasurementId(1L);
        m1.setTimestamp(new Date());
        m1.setTemperature(22.0);
        // Sensor 설정 (필수)
        Sensor sensor1 = new Sensor();
        sensor1.setSensorId(10L);
        m1.setSensor(sensor1);

        Measurement m2 = new Measurement();
        m2.setMeasurementId(2L);
        m2.setTimestamp(new Date());
        m2.setTemperature(24.0);
        // Sensor 설정 (필수)
        Sensor sensor2 = new Sensor();
        sensor2.setSensorId(11L);
        m2.setSensor(sensor2);

        when(measurementRepository.findAll()).thenReturn(Arrays.asList(m1, m2));
        assertEquals(2, measurementService.getAllMeasurements().size());
        verify(measurementRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteMeasurement() {
        doNothing().when(measurementRepository).deleteById(100L);
        measurementService.deleteMeasurement(100L);
        verify(measurementRepository, times(1)).deleteById(100L);
    }

    // 기타 getMeasurementsBySensorAndTimeRange 테스트 생략(필요 시 Sensor 객체 설정 확인)
}
