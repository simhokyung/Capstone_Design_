package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.DeviceControlRequestDto;
import com.example.airqualityplatform.service.DeviceControlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/smartthings")
@RequiredArgsConstructor
public class DeviceControlController {

    private final DeviceControlService controlService;

    /**
     * ▶ 수동 제어 (Pause + Command)
     * POST /api/smartthings/control/{deviceId}
     * - 자동제어 정책이 설정된 기기는 먼저 해제한 뒤,
     *   SmartThings API 로 명령을 전송합니다.
     */
    @PostMapping("/control/{deviceId}")
    public ResponseEntity<Void> manualControl(
            @PathVariable String deviceId,
            @Valid @RequestBody DeviceControlRequestDto dto
    ) {
        controlService.sendManualCommands(deviceId, dto);
        return ResponseEntity.ok().build();
    }

    /**
     * ▶ 자동/예약 제어 (Raw Command)
     * POST /api/smartthings/control/raw/{deviceId}
     * - 기존 정책은 건드리지 않고 바로 SmartThings API 로 명령만 전송합니다.
     */
    @PostMapping("/control/raw/{deviceId}")
    public ResponseEntity<Void> rawControl(
            @PathVariable String deviceId,
            @Valid @RequestBody DeviceControlRequestDto dto
    ) {
        controlService.sendCommandsRaw(deviceId, dto);
        return ResponseEntity.ok().build();
    }
}
