package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.response.HeatmapDataResponse;
import com.example.airqualityplatform.repository.MeasurementRepository;
import com.example.airqualityplatform.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HeatmapDataServiceTest {

    @InjectMocks
    private HeatmapDataService heatmapDataService;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetHeatmapDataByRoom() {
        Long roomId = 1L;
        Sensor s1 = new Sensor();
        s1.setSensorId(10L);
        s1.setXCoordinate(55.0);
        s1.setYCoordinate(75.0);

        Sensor s2 = new Sensor();
        s2.setSensorId(11L);
        s2.setXCoordinate(65.0);
        s2.setYCoordinate(85.0);

        when(sensorRepository.findByRoom_RoomId(roomId)).thenReturn(Arrays.asList(s1, s2));

        Measurement m1 = new Measurement();
        m1.setSensor(s1);
        m1.setPm25(50.0);
        Date now = new Date(1600000000000L);
        m1.setTimestamp(now);

        Measurement m2 = new Measurement();
        m2.setSensor(s2);
        m2.setPm25(60.0);
        Date now2 = new Date(1600003600000L);
        m2.setTimestamp(now2);

        when(measurementRepository.findTopBySensor_SensorIdOrderByTimestampDesc(10L)).thenReturn(m1);
        when(measurementRepository.findTopBySensor_SensorIdOrderByTimestampDesc(11L)).thenReturn(m2);

        var results = heatmapDataService.getHeatmapDataByRoom(roomId);
        assertEquals(2, results.size());

        HeatmapDataResponse r1 = results.get(0);
        HeatmapDataResponse r2 = results.get(1);

        // 첫 센서
        assertEquals(10L, r1.getSensorId());
        assertEquals(50.0, r1.getPm25());
        assertEquals(now, r1.getTimestamp());
        // 두 번째 센서
        assertEquals(11L, r2.getSensorId());
        assertEquals(60.0, r2.getPm25());
        assertEquals(now2, r2.getTimestamp());

        verify(sensorRepository, times(1)).findByRoom_RoomId(roomId);
        verify(measurementRepository, times(1)).findTopBySensor_SensorIdOrderByTimestampDesc(10L);
        verify(measurementRepository, times(1)).findTopBySensor_SensorIdOrderByTimestampDesc(11L);
    }
}
