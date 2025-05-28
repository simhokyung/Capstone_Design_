package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.domain.Room;
import com.example.airqualityplatform.dto.response.IndoorHistoryDto;
import com.example.airqualityplatform.dto.response.IndoorHistoryDto.RoomDto;
import com.example.airqualityplatform.dto.response.IndoorHistoryDto.SensorHeatDto;
import com.example.airqualityplatform.dto.response.IndoorHistoryDto.SensorPositionDto;
import com.example.airqualityplatform.dto.response.IndoorHistoryDto.TimeSliceDto;
import com.example.airqualityplatform.repository.RoomRepository;
import com.example.airqualityplatform.repository.MeasurementRepository;
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
    public IndoorHistoryDto getIndoorHistory(
            Instant start,
            Instant end,
            int intervalMinutes
    ) throws Exception {
        // 1) Room + Sensor 로드
        List<Room> rooms = roomRepo.findAll();

        // 2) 구조 DTO 매핑
        List<RoomDto> structure = new ArrayList<>();
        for (Room room : rooms) {
            List<List<Double>> poly = objectMapper.readValue(
                    room.getPolygon(),
                    new TypeReference<List<List<Double>>>() {}
            );
            List<SensorPositionDto> positions = room.getSensors().stream()
                    .map(s -> IndoorHistoryDto.SensorPositionDto.builder()
                            .sensorId(s.getSensorId())
                            .x(s.getXCoordinate())
                            .y(s.getYCoordinate())
                            .build())
                    .collect(Collectors.toList());

            structure.add(IndoorHistoryDto.RoomDto.builder()
                    .roomId(room.getRoomId())
                    .roomName(room.getRoomName())
                    .floorNumber(room.getFloorNumber())
                    .polygon(poly)
                    .sensors(positions)
                    .build());
        }

        // 3) 시간축 생성
        List<Instant> times = new ArrayList<>();
        for (Instant t = start; !t.isAfter(end); t = t.plus(intervalMinutes, ChronoUnit.MINUTES)) {
            times.add(t);
        }

        // 4) 슬라이스 매핑
        List<TimeSliceDto> slices = new ArrayList<>();
        for (Instant timePoint : times) {
            Date tp = Date.from(timePoint);
            List<SensorHeatDto> heats = new ArrayList<>();

            for (RoomDto rd : structure) {
                for (SensorPositionDto sp : rd.getSensors()) {
                    Measurement m = measurementRepo
                            .findTopBySensor_SensorIdAndTimestampLessThanEqualOrderByTimestampDesc(
                                    sp.getSensorId(), tp)
                            .orElse(null);
                    Double value = (m != null) ? m.getPm25_t() : null;
                    heats.add(IndoorHistoryDto.SensorHeatDto.builder()
                            .sensorId(sp.getSensorId())
                            .x(sp.getX())
                            .y(sp.getY())
                            .value(value)
                            .build());
                }
            }
            slices.add(IndoorHistoryDto.TimeSliceDto.builder()
                    .timestamp(timePoint)
                    .sensors(heats)
                    .build());
        }

        return IndoorHistoryDto.builder()
                .structure(structure)
                .slices(slices)
                .build();
    }
}