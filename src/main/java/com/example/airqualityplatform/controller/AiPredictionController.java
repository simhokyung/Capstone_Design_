// src/main/java/com/example/airqualityplatform/controller/AiPredictionController.java
package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.response.AiPredictionBatchDto;
import com.example.airqualityplatform.service.AiPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/predictions")
@RequiredArgsConstructor
public class AiPredictionController {

    private final AiPredictionService aiPredictionService;

    /** 최신 배치 + 센서별 예측 한 번에 내려주기 */
    @GetMapping("/latest")
    public ResponseEntity<AiPredictionBatchDto> getLatest() {
        return ResponseEntity.ok(aiPredictionService.getLatestBatchWithPredictions());
    }

    /** 특정 배치 ID에 대한 예측 한 번에 내려주기 */
    @GetMapping("/{batchId}")
    public ResponseEntity<AiPredictionBatchDto> getByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(aiPredictionService.getBatchWithPredictions(batchId));
    }
}
