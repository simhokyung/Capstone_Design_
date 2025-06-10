// src/main/java/com/example/airqualityplatform/controller/FilterStatusController.java
package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.domain.FilterStatus;
import com.example.airqualityplatform.dto.response.FilterStatusResponseDto;
import com.example.airqualityplatform.dto.mapper.FilterStatusMapper;
import com.example.airqualityplatform.service.FilterStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/filters")
@RequiredArgsConstructor
public class FilterStatusController {

    private final FilterStatusService service;

    /**
     * 1) SmartThings에서 최신 상태를 가져와 DB에 저장한 후 DTO로 반환
     */
    @GetMapping("/{filterId}/status")
    public ResponseEntity<FilterStatusResponseDto> fetchAndSaveStatus(
            @PathVariable Long filterId) {
        FilterStatus saved = service.fetchAndSaveStatus(filterId);
        return ResponseEntity.ok(FilterStatusMapper.toResponseDto(saved));
    }

    /**
     * 2) DB에 저장된 최신 상태를 단순 조회(저장 없이) 후 DTO로 반환
     */
    @GetMapping("/{filterId}/status/latest")
    public ResponseEntity<FilterStatusResponseDto> getLatestStatus(
            @PathVariable Long filterId) {
        FilterStatus latest = service.getLatestStatus(filterId);
        return ResponseEntity.ok(FilterStatusMapper.toResponseDto(latest));
    }

    /**
     * 3) 해당 필터의 모든 상태 이력을 내림차순(statusAt desc)으로 조회
     */
    @GetMapping("/{filterId}/status/history")
    public ResponseEntity<List<FilterStatusResponseDto>> listStatuses(
            @PathVariable Long filterId) {
        List<FilterStatusResponseDto> dtos = service.getStatuses(filterId).stream()
                .map(FilterStatusMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
