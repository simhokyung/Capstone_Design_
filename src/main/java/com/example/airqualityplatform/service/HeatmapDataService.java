package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.mapper.HeatmapDataMapper;
import com.example.airqualityplatform.dto.response.HeatmapDataResponse;
import com.example.airqualityplatform.repository.MeasurementRepository;
import com.example.airqualityplatform.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HeatmapDataService {

    private final SensorRepository sensorRepository;
    private final MeasurementRepository measurementRepository;

    @Autowired
    public HeatmapDataService(
            SensorRepository sensorRepository,
            MeasurementRepository measurementRepository
    ) {
        this.sensorRepository = sensorRepository;
        this.measurementRepository = measurementRepository;
    }

    /**
     * 주어진 roomId에 속한 모든 센서들의 최신 측정 데이터를 가져와,
     * HeatmapDataResponse 리스트로 반환합니다.
     */
    public List<HeatmapDataResponse> getHeatmapDataByRoom(Long roomId) {
        return sensorRepository.findByRoom_RoomId(roomId).stream()
                .map(sensor -> measurementRepository
                        .findTopBySensor_SensorIdOrderByTimestampDesc(sensor.getSensorId())
                        .map(measurement -> HeatmapDataMapper.toDto(sensor, measurement))
                        .orElse(null)
                )
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
