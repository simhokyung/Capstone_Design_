package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.ai.PredictionResponse;
import com.example.airqualityplatform.ai.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private PredictionService predictionService;

    // 예측 API 엔드포인트: /ai/predictions?sensorId=1&hours=3
    @GetMapping("/predictions")
    public List<PredictionResponse> getPredictions(
            @RequestParam Long sensorId,
            @RequestParam(defaultValue = "3") int hours) {
        return predictionService.predictAirQuality(sensorId, hours);
    }
}