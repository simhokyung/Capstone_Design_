package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.TokenRequestDto;
import com.example.airqualityplatform.service.DeviceStatusService;
import com.example.airqualityplatform.service.SmartThingsTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/smartthings")
public class TokenController {

    private final SmartThingsTokenService tokenService;
    private final DeviceStatusService statusService;

    public TokenController(SmartThingsTokenService tokenService,
                           DeviceStatusService statusService) {
        this.tokenService = tokenService;
        this.statusService = statusService;
    }

    /**
     * 외부에서 24h마다 호출 → 토큰 저장 → 즉시 전체 디바이스 상태 동기화
     */
    @PostMapping("/token")
    public ResponseEntity<Void> receiveToken(@RequestBody TokenRequestDto dto) {
        // 1) 토큰 저장
        tokenService.saveToken(dto.getToken());

        // 2) 토큰 갱신 직후, 모든 디바이스 상태를 SmartThings에서 다시 pull & 저장
        statusService.fetchAndSaveAllStatuses();

        return ResponseEntity.ok().build();
    }
}
