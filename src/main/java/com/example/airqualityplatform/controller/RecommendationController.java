package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.response.PurifierRecommendation;
import com.example.airqualityplatform.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    /**
     * GET /recommendations/purifier?roomId={roomId}&days={days}
     * 주어진 roomId와 기간(days)을 기반으로 공기청정기 위치 추천 결과를 반환합니다.
     */
    @GetMapping("/purifier")
    public PurifierRecommendation getPurifierRecommendation(
            @RequestParam Long roomId,
            @RequestParam(defaultValue = "7") int days) {
        return recommendationService.getPurifierRecommendation(roomId, days);
    }
}
