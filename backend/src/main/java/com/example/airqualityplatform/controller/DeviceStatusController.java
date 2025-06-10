package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.response.DeviceStatusResponseDto;
import com.example.airqualityplatform.dto.mapper.DeviceStatusResponseMapper;
import com.example.airqualityplatform.domain.DeviceStatus;
import com.example.airqualityplatform.service.DeviceStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/smartthings")
public class DeviceStatusController {

    private final DeviceStatusService service;

    public DeviceStatusController(DeviceStatusService service) {
        this.service = service;
    }

    /**
     * GET /api/smartthings/status/{deviceId}
     * 저장된 토큰을 사용해 SmartThings에서 상태를 가져와 DB에 저장 후, DTO로 반환
     */
    @GetMapping("/status/{deviceId}")
    public ResponseEntity<DeviceStatusResponseDto> getStatus(@PathVariable String deviceId) {
        DeviceStatus saved = service.fetchAndSaveStatusByDeviceId(deviceId);
        return ResponseEntity.ok(DeviceStatusResponseMapper.toDto(saved));
    }
}