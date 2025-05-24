// src/main/java/com/example/airqualityplatform/controller/InterestRegionController.java
package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.InterestRegionRequestDto;
import com.example.airqualityplatform.dto.response.AirQualityResponseDto;
import com.example.airqualityplatform.dto.response.InterestRegionResponseDto;
import com.example.airqualityplatform.dto.response.RegionForecastResponseDto;
import com.example.airqualityplatform.service.AirQualityService;
import com.example.airqualityplatform.service.InterestRegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/regions")
@RequiredArgsConstructor
public class InterestRegionController {

    private final InterestRegionService regionService;
    private final AirQualityService aqService;

    @PostMapping
    public ResponseEntity<InterestRegionResponseDto> addRegion(
            @PathVariable Long userId,
            @Valid @RequestBody InterestRegionRequestDto dto) {

        var created = regionService.create(userId, dto);
        URI location = URI.create(String.format("/users/%d/regions/%d",
                userId, created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<InterestRegionResponseDto>> listByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(regionService.findByUser(userId));
    }

    @DeleteMapping("/{regionId}")
    public ResponseEntity<Void> removeRegion(
            @PathVariable Long userId,
            @PathVariable Long regionId) {
        regionService.delete(userId, regionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/air-quality")
    public ResponseEntity<List<AirQualityResponseDto>> getAirQuality(
            @PathVariable Long userId) {
        return ResponseEntity.ok(aqService.getAirQualityForUser(userId));
    }

    @GetMapping("/forecast")
    public ResponseEntity<List<RegionForecastResponseDto>> getForecast(
            @PathVariable Long userId) {
        return ResponseEntity.ok(aqService.getForecastForUser(userId));
    }
}
