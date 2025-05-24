// src/main/java/com/example/airqualityplatform/controller/DeviceControlController.java
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
     * ▶ 수동제어 (Pause + Command)
     * POST /api/smartthings/control/{deviceId}
     *   - 자동제어 정책이 설정된 기기는 먼저 해제한 뒤,
     *     SmartThings API 로 수동 제어(on/off, fanMode) 명령을 전송합니다.
     */
    @PostMapping("/control/{deviceId}")
    public ResponseEntity<Void> controlDevice(
            @PathVariable String deviceId,
            @Valid @RequestBody DeviceControlRequestDto dto
    ) {
        controlService.sendManualCommands(deviceId, dto);
        return ResponseEntity.ok().build();
    }

    /**
     * ▶ 자동제어 재개 (Resume)
     * PATCH /api/smartthings/control/{deviceId}/resume/{policyId}
     *   - 일시 해제했던 정책(policyId)을 기기에 다시 연결합니다.
     */
    @PatchMapping("/control/{deviceId}/resume/{policyId}")
    public ResponseEntity<Void> resumeControl(
            @PathVariable String deviceId,
            @PathVariable Long policyId
    ) {
        controlService.resumeAutoControl(deviceId, policyId);
        return ResponseEntity.noContent().build();
    }
}
