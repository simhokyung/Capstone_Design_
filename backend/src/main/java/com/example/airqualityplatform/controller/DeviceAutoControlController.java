package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.DeviceAutoControlRequestDto;
import com.example.airqualityplatform.dto.response.DeviceAutoControlResponseDto;
import com.example.airqualityplatform.service.DeviceAutoControlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
public class DeviceAutoControlController {

    private final DeviceAutoControlService autoControlService;

    @PostMapping
    public ResponseEntity<DeviceAutoControlResponseDto> createAutoControl(
            @Valid @RequestBody DeviceAutoControlRequestDto dto) {
        DeviceAutoControlResponseDto created = autoControlService.createAutoControl(dto);
        URI location = URI.create("/policies/" + created.getControlId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<DeviceAutoControlResponseDto>> getAllAutoControls() {
        return ResponseEntity.ok(autoControlService.getAllAutoControls());
    }

    @GetMapping("/{controlId}")
    public ResponseEntity<DeviceAutoControlResponseDto> getAutoControlById(@PathVariable Long controlId) {
        return ResponseEntity.ok(autoControlService.getAutoControlById(controlId));
    }

    @PutMapping("/{controlId}")
    public ResponseEntity<DeviceAutoControlResponseDto> updateAutoControl(
            @PathVariable Long controlId,
            @Valid @RequestBody DeviceAutoControlRequestDto dto) {
        return ResponseEntity.ok(autoControlService.updateAutoControl(controlId, dto));
    }

    @DeleteMapping("/{controlId}")
    public ResponseEntity<Void> deleteAutoControl(@PathVariable Long controlId) {
        autoControlService.deleteAutoControl(controlId);
        return ResponseEntity.noContent().build();
    }
}
