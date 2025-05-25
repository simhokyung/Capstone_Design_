// src/main/java/com/example/airqualityplatform/controller/AiControlController.java
package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.AiControlBatchRequestDto;
import com.example.airqualityplatform.dto.response.AiControlBatchResponseDto;
import com.example.airqualityplatform.service.AiControlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/ai/control")
@RequiredArgsConstructor
public class AiControlController {

    private final AiControlService controlService;

    /** AI → 최적제어 스케줄 수신 */
    @PostMapping
    public ResponseEntity<AiControlBatchResponseDto> receiveControl(
            @Valid @RequestBody AiControlBatchRequestDto dto
    ) {
        AiControlBatchResponseDto created = controlService.ingestControl(dto);
        URI location = URI.create("/api/ai/control/" + created.getBatchId());
        return ResponseEntity.created(location).body(created);
    }

    /** 최신 배치 조회 */
    @GetMapping("/latest")
    public ResponseEntity<AiControlBatchResponseDto> latest() {
        return ResponseEntity.ok(controlService.getLatestBatch());
    }
}
