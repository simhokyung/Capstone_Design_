package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.response.HeatmapDataResponse;
import com.example.airqualityplatform.service.HeatmapDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/heatmap")
public class HeatmapController {

    @Autowired
    private HeatmapDataService heatmapDataService;

    /**
     * GET /heatmap?roomId={roomId}
     * 해당 roomId에 속한 센서들의 최신 측정 데이터를 반환합니다.
     * 프론트엔드에서는 이 데이터를 바탕으로 IDW 보간 등을 통해 히트맵을 생성할 수 있습니다.
     */
    @GetMapping
    public List<HeatmapDataResponse> getHeatmapData(@RequestParam Long roomId) {
        return heatmapDataService.getHeatmapDataByRoom(roomId);
    }
}
