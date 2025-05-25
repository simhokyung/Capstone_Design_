// src/main/java/com/example/airqualityplatform/service/AiPredictionService.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.AiPredictionBatch;
import com.example.airqualityplatform.domain.SensorPrediction;
import com.example.airqualityplatform.dto.request.AiPredictionBatchRequestDto;
import com.example.airqualityplatform.dto.response.AiPredictionBatchResponseDto;
import com.example.airqualityplatform.dto.response.SensorPredictionDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.AiPredictionBatchRepository;
import com.example.airqualityplatform.repository.SensorPredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiPredictionService {

    private final AiPredictionBatchRepository batchRepo;
    private final SensorPredictionRepository  sensorRepo;

    /* ───────── AI → 예측 수신 ───────── */
    @Transactional
    public AiPredictionBatchResponseDto ingestPrediction(AiPredictionBatchRequestDto dto) {

        /* 1. 배치 저장 */
        AiPredictionBatch batch = new AiPredictionBatch();
        batch.setTimestamp(Instant.parse(dto.getTimestamp()));
        batchRepo.save(batch);

        /* 2. 센서별 예측 저장 */
        dto.getPredictions().forEach(p -> {
            SensorPrediction sp = SensorPrediction.builder()
                    .batch(batch)
                    .sensorId(p.getSensorId())
                    .deviceId(p.getDeviceId())
                    .voc(p.getVoc())
                    .temperature(p.getTemperature())
                    .humidity(p.getHumidity())
                    .co2(p.getCo2())
                    .pm25_t(p.getPm25_t())
                    .pm100_t(p.getPm100_t())
                    .build();
            sensorRepo.save(sp);
            batch.getPredictions().add(sp);
        });

        return toDto(batch);
    }

    /* ───────── 조회 ───────── */

    @Transactional(readOnly = true)
    public AiPredictionBatchResponseDto getLatestBatchWithPredictions() {
        AiPredictionBatch batch = batchRepo.findTopByOrderByTimestampDesc();
        if (batch == null)
            throw new ResourceNotFoundException("예측 배치가 없습니다.");
        return toDto(batch);
    }

    @Transactional(readOnly = true)
    public AiPredictionBatchResponseDto getBatchWithPredictions(Long batchId) {
        AiPredictionBatch batch = batchRepo.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("batchId=" + batchId + " 배치가 없습니다."));
        return toDto(batch);
    }

    /* ───────── 변환 유틸 ───────── */

    private AiPredictionBatchResponseDto toDto(AiPredictionBatch b) {
        return AiPredictionBatchResponseDto.builder()
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
