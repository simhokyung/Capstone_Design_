// src/main/java/com/example/airqualityplatform/service/MeasurementService.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.mapper.MeasurementMapper;
import com.example.airqualityplatform.dto.request.MeasurementRequestDto;
import com.example.airqualityplatform.dto.response.MeasurementResponseDto;
import com.example.airqualityplatform.repository.MeasurementRepository;
import com.example.airqualityplatform.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final SensorRepository sensorRepository;

    public MeasurementResponseDto createMeasurement(MeasurementRequestDto dto) {
        log.debug("createMeasurement 호출, dto={}", dto);

        Sensor sensor = sensorRepository.findBySensorId(dto.getSensorId())
                .orElseThrow(() -> {
                    log.error("Sensor not found for sensorId={}", dto.getSensorId());
                    return new IllegalArgumentException(
                            "Sensor not found with sensorId: " + dto.getSensorId());
                });

        Measurement m = MeasurementMapper.toEntity(dto, null);
        m.setSensor(sensor);

        Measurement saved = measurementRepository.save(m);
        log.info("저장된 measurement: id={}, sensorId={}, timestamp={}",
                saved.getMeasurementId(), dto.getSensorId(), saved.getTimestamp());
        return MeasurementMapper.toResponseDto(saved);
    }

    public List<MeasurementResponseDto> getAllMeasurements() {
        return measurementRepository.findAll().stream()
                .map(MeasurementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public MeasurementResponseDto getMeasurementById(Long id) {
        Measurement m = measurementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Measurement not found: " + id));
        return MeasurementMapper.toResponseDto(m);
    }

    public void deleteMeasurement(Long id) {
        if (!measurementRepository.existsById(id)) {
            throw new IllegalArgumentException("Measurement not found: " + id);
        }
        measurementRepository.deleteById(id);
    }

    public List<MeasurementResponseDto> getMeasurementsBySensorAndTimeRange(
            Long sensorId, Date start, Date end) {
        return measurementRepository
                .findBySensor_SensorIdAndTimestampBetween(sensorId, start, end)
                .stream()
                .map(MeasurementMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
