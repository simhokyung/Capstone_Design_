// src/main/java/com/example/airqualityplatform/dto/response/AiControlBatchResponseDto.java
package com.example.airqualityplatform.dto.response;

import com.example.airqualityplatform.domain.AiControlBatch;
import com.example.airqualityplatform.domain.AiControlSegment;
import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class AiControlBatchResponseDto {
    private Long batchId;
    private Instant timestamp;
    private String deviceId;
    private List<Segment> segments;

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class Segment {
        private Integer startMinute;
        private Integer endMinute;
        private String airPurifier;
        private String fanMode;
        private Boolean ventilation;
    }

    /** Entity → DTO 변환 헬퍼 */
    public static AiControlBatchResponseDto fromEntity(AiControlBatch batch) {
        return AiControlBatchResponseDto.builder()
                .batchId(batch.getBatchId())
                .timestamp(batch.getTimestamp())
                .deviceId(batch.getDeviceId())
                .segments(batch.getSegments().stream()
                        .map(AiControlBatchResponseDto::toSegmentDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private static Segment toSegmentDto(AiControlSegment seg) {
        return new Segment(
                seg.getStartMinute(),
                seg.getEndMinute(),
                seg.getAirPurifier(),
                seg.getFanMode(),
                seg.getVentilation()
        );
    }
}
