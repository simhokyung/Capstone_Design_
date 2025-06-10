// src/main/java/com/example/airqualityplatform/dto/response/AiPredictionBatchDto.java
package com.example.airqualityplatform.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiPredictionBatchResponseDto {
    private Long batchId;
    private Instant timestamp;
    private List<SensorPredictionDto> predictions;
}
