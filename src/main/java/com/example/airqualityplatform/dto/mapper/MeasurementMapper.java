// src/main/java/com/example/airqualityplatform/dto/mapper/MeasurementMapper.java
package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.dto.request.MeasurementRequestDto;
import com.example.airqualityplatform.dto.response.MeasurementResponseDto;

import java.util.Date;

public class MeasurementMapper {

    public static Measurement toEntity(MeasurementRequestDto dto, Measurement m) {
        if (m == null) {
            m = new Measurement();
        }

        // 1) ISO timestamp 우선
        if (dto.getTimestamp() != null) {
            m.setTimestamp(dto.getTimestamp());
        }
        // 2) 없으면 unix → Date
        else if (dto.getUnix() != null) {
            m.setTimestamp(new Date(dto.getUnix() * 1000));
        }
        // 3) 둘 다 없으면 @PrePersist가 현재 시각으로 채워 줌

        m.setTemperature(dto.getTemperature());
        m.setHumidity(dto.getHumidity());
        m.setCo2(dto.getCo2());
        m.setVoc(dto.getVoc());
        m.setPm25_b(dto.getPm25_b());
        m.setPm25_m(dto.getPm25_m());
        m.setPm25_t(dto.getPm25_t());
        m.setPm100_b(dto.getPm100_b());
        m.setPm100_m(dto.getPm100_m());
        m.setPm100_t(dto.getPm100_t());

        return m;
    }

    public static MeasurementResponseDto toResponseDto(Measurement m) {
        if (m == null) return null;
        MeasurementResponseDto dto = new MeasurementResponseDto();

        dto.setMeasurementId(m.getMeasurementId());
        dto.setSensorId(m.getSensor().getSensorId());
        dto.setTimestamp(m.getTimestamp());
        dto.setTemperature(m.getTemperature());
        dto.setHumidity(m.getHumidity());
        dto.setCo2(m.getCo2());
        dto.setVoc(m.getVoc());
        dto.setPm25_b(m.getPm25_b());
        dto.setPm25_m(m.getPm25_m());
        dto.setPm25_t(m.getPm25_t());
        dto.setPm100_b(m.getPm100_b());
        dto.setPm100_m(m.getPm100_m());
        dto.setPm100_t(m.getPm100_t());

        return dto;
    }
}
