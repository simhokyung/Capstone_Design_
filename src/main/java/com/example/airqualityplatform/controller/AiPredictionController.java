// src/main/java/com/example/airqualityplatform/controller/AiPredictionController.java
package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.AiPredictionBatchRequestDto;
import com.example.airqualityplatform.dto.response.AiPredictionBatchResponseDto;
import com.example.airqualityplatform.service.AiPredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/ai/predictions")
@RequiredArgsConstructor
public class AiPredictionController {

    private final AiPredictionService svc;

    /* ---------- AI → 예측 수신 ---------- */
    @PostMapping
    public ResponseEntity<AiPredictionBatchResponseDto> receive(
            @Valid @RequestBody AiPredictionBatchRequestDto dto) {

        AiPredictionBatchResponseDto saved = svc.ingestPrediction(dto);
        URI location = URI.create("/ai/predictions/" + saved.getBatchId());
        return ResponseEntity.created(location).body(saved);
    }

    /* ---------- 최신 조회 ---------- */
    @GetMapping("/latest")
    public ResponseEntity<AiPredictionBatchResponseDto> latest() {
        return ResponseEntity.ok(svc.getLatestBatchWithPredictions());
    }

    /* ---------- ID 조회 ---------- */
    @GetMapping("/{batchId}")
    public ResponseEntity<AiPredictionBatchResponseDto> byId(@PathVariable Long batchId) {
        return ResponseEntity.ok(svc.getBatchWithPredictions(batchId));
    }
}
