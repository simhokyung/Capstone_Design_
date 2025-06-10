package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.Filter;
import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.repository.DeviceRepository;
import com.example.airqualityplatform.repository.MeasurementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FilterLifeServiceTest {

    @InjectMocks
    private FilterLifeService filterLifeService;

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalculateFilterSoiling_NoMeasurements() {
        // Device와 Filter 세팅
        Long deviceId = 1L;
        Device device = new Device();
        device.setDeviceId(deviceId);
        Filter filter = new Filter();
        filter.setRecommendedLifetime(100.0);
        device.setFilters(Arrays.asList(filter));

        when(deviceRepository.findById(deviceId)).thenReturn(java.util.Optional.of(device));
        when(measurementRepository.findBySensor_Device_DeviceIdOrderByTimestampAsc(deviceId))
                .thenReturn(new ArrayList<>());

        double result = filterLifeService.calculateFilterSoiling(deviceId);
        assertEquals(0.0, result);
    }

    @Test
    public void testCalculateFilterSoiling_WithMeasurements() {
        Long deviceId = 1L;
        Device device = new Device();
        device.setDeviceId(deviceId);
        Filter filter = new Filter();
        filter.setRecommendedLifetime(100.0);
        device.setFilters(Arrays.asList(filter));

        when(deviceRepository.findById(deviceId)).thenReturn(java.util.Optional.of(device));

        // 측정값
        Measurement m1 = new Measurement();
        m1.setPm25(25.0);
        m1.setFanSpeed(2); // stepFactor = 1.0
        m1.setTimestamp(new Date(1600000000000L));

        Measurement m2 = new Measurement();
        m2.setPm25(35.0);
        m2.setFanSpeed(3); // stepFactor = 1.5
        m2.setTimestamp(new Date(1600003600000L)); // 1시간 뒤

        when(measurementRepository.findBySensor_Device_DeviceIdOrderByTimestampAsc(deviceId))
                .thenReturn(Arrays.asList(m1, m2));

        double result = filterLifeService.calculateFilterSoiling(deviceId);
        // 대략 (pm25/PM_REF) * stepFactor * deltaHours / recommendedLifetime 계산
        // PM_REF=25.0, recommendedLifetime=100.0
        // 첫 구간은 m1 -> m2, deltaHours=1
        // partial = (35/25)*1.5*1 = (1.4)*1.5 = 2.1
        // sum=2.1; soiling=2.1/100=0.021
        // (단, m1부터 m2까지 하나의 구간만 있다고 가정)

        assertTrue(result > 0.02 && result < 0.03, "Soiling should be around 0.021");
    }
}
