// src/main/java/com/example/airqualityplatform/controller/MeasurementController.java
package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.MeasurementRequestDto;
import com.example.airqualityplatform.dto.response.MeasurementResponseDto;
import com.example.airqualityplatform.service.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/measurements")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementService measurementService;

    @PostMapping
    public ResponseEntity<MeasurementResponseDto> create(
            @RequestBody MeasurementRequestDto dto) {
        MeasurementResponseDto saved = measurementService.createMeasurement(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<MeasurementResponseDto> all() {
        return measurementService.getAllMeasurements();
    }

    @GetMapping("/{id}")
    public MeasurementResponseDto one(@PathVariable Long id) {
        return measurementService.getMeasurementById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        measurementService.deleteMeasurement(id);
    }

    @GetMapping("/sensor/{sensorId}")
    public List<MeasurementResponseDto> bySensor(
            @PathVariable Long sensorId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime
    ) {
        Date start = (startTime != null) ? new Date(startTime) : new Date(0);
        Date end   = (endTime   != null) ? new Date(endTime)   : new Date();
        return measurementService
                .getMeasurementsBySensorAndTimeRange(sensorId, start, end);
    }
}
