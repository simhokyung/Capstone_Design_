package com.example.airqualityplatform.dto.response;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IndoorRoomHistoryDto {
    private RoomDto room;
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
        private List<SensorHistoryDto> sensors;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SensorHistoryDto {
        private Long sensorId;
        private Double x;
        private Double y;
        private Double pm25;
        private Double pm10;
        private Double co2;
        private Double voc;
    }
}