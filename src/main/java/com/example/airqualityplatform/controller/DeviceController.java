package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.DeviceRequestDto;
import com.example.airqualityplatform.dto.response.DeviceResponseDto;
import com.example.airqualityplatform.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<DeviceResponseDto> createDevice(
            @Valid @RequestBody DeviceRequestDto dto) {
        DeviceResponseDto created = deviceService.createDevice(dto);
        URI location = URI.create("/devices/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponseDto>> getAllDevices() {
        List<DeviceResponseDto> list = deviceService.getAllDevices();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> getDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @GetMapping("/by-external/{deviceId}")
    public ResponseEntity<DeviceResponseDto> getByExternal(
            @PathVariable String deviceId) {
        return ResponseEntity.ok(deviceService.getByExternalDeviceId(deviceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> updateDevice(
            @PathVariable Long id,
            @Valid @RequestBody DeviceRequestDto dto) {
        return ResponseEntity.ok(deviceService.updateDevice(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}
