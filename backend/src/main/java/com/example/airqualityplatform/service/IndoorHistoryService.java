package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.domain.Room;
import com.example.airqualityplatform.dto.response.IndoorRoomHistoryDto;
import com.example.airqualityplatform.dto.response.IndoorRoomHistoryDto.RoomDto;
import com.example.airqualityplatform.dto.response.IndoorRoomHistoryDto.SensorHistoryDto;
import com.example.airqualityplatform.dto.response.IndoorRoomHistoryDto.SensorPositionDto;
import com.example.airqualityplatform.dto.response.IndoorRoomHistoryDto.TimeSliceDto;
import com.example.airqualityplatform.repository.MeasurementRepository;
import com.example.airqualityplatform.repository.RoomRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndoorHistoryService {

    private final RoomRepository roomRepo;
    private final MeasurementRepository measurementRepo;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public IndoorRoomHistoryDto getRoomHistory(
            Long roomId,
            Instant start,
            Instant end,
            int intervalMinutes
    ) throws Exception {
        // 1) 방 + 센서 로드
        Room room = roomRepo.findWithSensorsByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        // 2) RoomDto 매핑
        List<List<Double>> poly = objectMapper.readValue(
                room.getPolygon(), new TypeReference<List<List<Double>>>() {}
        );
        List<SensorPositionDto> positions = room.getSensors().stream()
                .map(s -> SensorPositionDto.builder()
                        .sensorId(s.getSensorId())
                        .x(s.getXCoordinate())
                        .y(s.getYCoordinate())
                        .build())
                .collect(Collectors.toList());

        RoomDto roomDto = RoomDto.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .floorNumber(room.getFloorNumber())
                .polygon(poly)
                .sensors(positions)
                .build();

        // 3) 시간축 생성
        List<Instant> times = new ArrayList<>();
        for (Instant t = start; !t.isAfter(end); t = t.plus(intervalMinutes, ChronoUnit.MINUTES)) {
            times.add(t);
        }

        // 4) TimeSliceDto 매핑
        List<TimeSliceDto> slices = new ArrayList<>();
        for (Instant timePoint : times) {
            Date tp = Date.from(timePoint);
            List<SensorHistoryDto> sensorData = new ArrayList<>();

            for (SensorPositionDto sp : positions) {
                Measurement m = measurementRepo
                        .findTopBySensor_SensorIdAndTimestampLessThanEqualOrderByTimestampDesc(
                                sp.getSensorId(), tp)
                        .orElse(null);
                Double pm25 = m != null ? m.getPm25_t() : null;
                Double pm10 = m != null ? m.getPm100_t() : null;
                Double co2  = m != null ? m.getCo2()   : null;
                Double voc  = m != null ? m.getVoc()   : null;

                sensorData.add(SensorHistoryDto.builder()
                        .sensorId(sp.getSensorId())
                        .x(sp.getX())
                        .y(sp.getY())
                        .pm25(pm25)
                        .pm10(pm10)
                        .co2(co2)
                        .voc(voc)
                        .build());
            }
            slices.add(TimeSliceDto.builder()
                    .timestamp(timePoint)
                    .sensors(sensorData)
                    .build());
        }

        return IndoorRoomHistoryDto.builder()
                .room(roomDto)
                .slices(slices)
                .build();
    }
}