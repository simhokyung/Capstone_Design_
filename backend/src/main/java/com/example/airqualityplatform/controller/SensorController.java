package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.SensorRequestDto;
import com.example.airqualityplatform.dto.response.SensorResponseDto;
import com.example.airqualityplatform.service.SensorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @PostMapping
    public ResponseEntity<SensorResponseDto> createSensor(
            @Valid @RequestBody SensorRequestDto dto) {
        SensorResponseDto created = sensorService.createSensor(dto);
        URI location = URI.create("/sensors/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<SensorResponseDto>> getAllSensors() {
        return ResponseEntity.ok(sensorService.getAllSensors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorResponseDto> getSensorById(@PathVariable Long id) {
        return ResponseEntity.ok(sensorService.getSensorById(id));
    }

    @GetMapping("/by-external/{sensorId}")
    public ResponseEntity<SensorResponseDto> getByExternalSensorId(@PathVariable Long sensorId) {
        return ResponseEntity.ok(sensorService.getByExternalSensorId(sensorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorResponseDto> updateSensor(
            @PathVariable Long id,
            @Valid @RequestBody SensorRequestDto dto) {
        return ResponseEntity.ok(sensorService.updateSensor(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable Long id) {
        sensorService.deleteSensor(id);
        return ResponseEntity.noContent().build();
    }
}
