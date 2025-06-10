// src/main/java/com/example/airqualityplatform/controller/FilterLifeController.java
package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.response.FilterLifeResponseDto;
import com.example.airqualityplatform.service.FilterLifeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filters/{filterId}/life")
@RequiredArgsConstructor
public class FilterLifeController {

    private final FilterLifeService filterLifeService;

    /**
     * GET /api/filters/{filterId}/life
     * 최신 및 이력을 기반으로 필터 잔여 수명 계산 후 반환
     */
    @GetMapping
    public ResponseEntity<FilterLifeResponseDto> getFilterLife(@PathVariable Long filterId) {
        FilterLifeResponseDto dto = filterLifeService.calculateFilterLife(filterId);
        return ResponseEntity.ok(dto);
    }
}
