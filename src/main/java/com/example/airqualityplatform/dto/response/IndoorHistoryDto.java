package com.example.airqualityplatform.dto.response;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IndoorHistoryDto {
    private List<RoomDto> structure;
    private List<TimeSliceDto> slices;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RoomDto {
        private Long roomId;
        private String roomName;
        private Integer floorNumber;
        private List<List<Double>> polygon;
        private List<SensorPositionDto> sensors;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SensorPositionDto {
        private Long sensorId;
        private Double x;
        private Double y;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TimeSliceDto {
        private Instant timestamp;
        private List<SensorHeatDto> sensors;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SensorHeatDto {
        private Long sensorId;
        private Double x;
        private Double y;
        private Double value;
    }
}
