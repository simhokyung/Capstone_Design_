// src/main/java/com/example/airqualityplatform/service/AiPredictionService.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.AiPredictionBatch;
import com.example.airqualityplatform.domain.SensorPrediction;
import com.example.airqualityplatform.dto.response.AiPredictionBatchDto;
import com.example.airqualityplatform.dto.response.SensorPredictionDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.AiPredictionBatchRepository;
import com.example.airqualityplatform.repository.SensorPredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiPredictionService {

    private final AiPredictionBatchRepository batchRepo;
    private final SensorPredictionRepository sensorRepo;

    @Transactional(readOnly = true)
    public AiPredictionBatchDto getLatestBatchWithPredictions() {
        AiPredictionBatch batch = batchRepo.findTopByOrderByTimestampDesc();
        if (batch == null) {
            throw new ResourceNotFoundException("예측 배치가 없습니다.");
        }
        return toDto(batch);
    }

    @Transactional(readOnly = true)
    public AiPredictionBatchDto getBatchWithPredictions(Long batchId) {
        AiPredictionBatch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("배치를 찾을 수 없습니다. batchId=" + batchId));
        return toDto(batch);
    }

    private AiPredictionBatchDto toDto(AiPredictionBatch b) {
        return AiPredictionBatchDto.builder()
                .batchId(b.getBatchId())
                .timestamp(b.getTimestamp())
                .predictions(
                        b.getPredictions().stream()
                                .map(this::toSensorDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private SensorPredictionDto toSensorDto(SensorPrediction s) {
        return SensorPredictionDto.builder()
                .sensorId(s.getSensorId())
                .deviceId(s.getDeviceId())
                .voc(s.getVoc())
                .temperature(s.getTemperature())
                .humidity(s.getHumidity())
                .co2(s.getCo2())
                .pm25_t(s.getPm25_t())
                .pm100_t(s.getPm100_t())
                .build();
    }
}
