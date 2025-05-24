package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.response.HeatmapDataResponse;

public class HeatmapDataMapper {

    /**
     * Sensor와 그 센서의 최신 Measurement로부터 HeatmapDataResponse DTO를 생성합니다.
     */
    public static HeatmapDataResponse toDto(Sensor sensor, Measurement measurement) {
        return new HeatmapDataResponse(
                sensor.getSensorId(),
                sensor.getXCoordinate(),
                sensor.getYCoordinate(),
                measurement.getPm25_m(),
                measurement.getTimestamp()
        );
    }
}
