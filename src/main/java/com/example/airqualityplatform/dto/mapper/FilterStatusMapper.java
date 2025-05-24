// src/main/java/com/example/airqualityplatform/dto/mapper/FilterStatusMapper.java
package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.FilterStatus;
import com.example.airqualityplatform.dto.external.FilterStatusExternalDto;
import com.example.airqualityplatform.dto.response.FilterStatusResponseDto;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.Stream;

public class FilterStatusMapper {

    /**
     * 외부 DTO + Filter 엔티티 → FilterStatus 엔티티
     */
    public static FilterStatus toEntity(
            FilterStatusExternalDto ext,
            com.example.airqualityplatform.domain.Filter filter
    ) {
        // JSON 구조: components.main."custom.hepaFilter" → Main.getHepaFilter()
        var hf = ext.getComponents()
                .getMain()
                .getHepaFilter();
        Instant now = Instant.now();

        // 빌더로 필드 세팅 (usage → usedHours, usageStep → usedHoursStep)
        FilterStatus s = FilterStatus.builder()
                .filter(filter)
                .capacity(hf.getHepaFilterCapacity().getValue())
                .capacityUnit(hf.getHepaFilterCapacity().getUnit())
                .usedHours(hf.getHepaFilterUsage().getValue())
                .usedHoursStep(hf.getHepaFilterUsageStep().getValue())
                .status(hf.getHepaFilterStatus().getValue())
                .resetTypes(hf.getHepaFilterResetType().getValue())
                .lastResetDate(hf.getHepaFilterLastResetDate().getValue())
                .fetchedAt(now)
                .build();

        // timestamp 중 가장 최신 시각을 statusAt에 설정
        Instant[] timestamps = {
                parse(hf.getHepaFilterCapacity().getTimestamp()),
                parse(hf.getHepaFilterUsage().getTimestamp()),
                parse(hf.getHepaFilterUsageStep().getTimestamp()),
                parse(hf.getHepaFilterStatus().getTimestamp()),
                parse(hf.getHepaFilterResetType().getTimestamp()),
                parse(hf.getHepaFilterLastResetDate().getTimestamp())
        };
        Instant statusAt = Stream.of(timestamps)
                .filter(Objects::nonNull)
                .max(Instant::compareTo)
                .orElse(now);
        s.setStatusAt(statusAt);

        return s;
    }

    /** ISO8601 문자열 → Instant 변환 (null 안전) */
    private static Instant parse(String ts) {
        if (ts == null) return null;
        return OffsetDateTime.parse(ts).toInstant();
    }

    /**
     * FilterStatus 엔티티 → FilterStatusResponseDto
     */
    public static FilterStatusResponseDto toResponseDto(FilterStatus s) {
        FilterStatusResponseDto dto = new FilterStatusResponseDto();
        dto.setId(s.getId());
        dto.setFilterId(s.getFilter().getFilterId());
        dto.setCapacity(s.getCapacity());
        dto.setCapacityUnit(s.getCapacityUnit());
        dto.setUsedHours(s.getUsedHours());
        dto.setUsedHoursStep(s.getUsedHoursStep());
        dto.setStatus(s.getStatus());
        dto.setResetTypes(s.getResetTypes());
        dto.setLastResetDate(s.getLastResetDate());
        dto.setFetchedAt(s.getFetchedAt());
        dto.setStatusAt(s.getStatusAt());
        return dto;
    }
}
