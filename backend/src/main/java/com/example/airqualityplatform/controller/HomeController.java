package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.HomeRequestDto;
import com.example.airqualityplatform.dto.response.HomeResponseDto;
import com.example.airqualityplatform.service.HomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/homes")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @PostMapping
    public ResponseEntity<HomeResponseDto> createHome(
            @Valid @RequestBody HomeRequestDto dto) {
        HomeResponseDto created = homeService.createHome(dto);
        URI location = URI.create("/homes/" + created.getHomeId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<HomeResponseDto>> getAllHomes() {
        return ResponseEntity.ok(homeService.getAllHomes());
    }

    @GetMapping("/{homeId}")
    public ResponseEntity<HomeResponseDto> getHomeById(@PathVariable Long homeId) {
        return ResponseEntity.ok(homeService.getHomeById(homeId));
    }

    @PutMapping("/{homeId}")
    public ResponseEntity<HomeResponseDto> updateHome(
            @PathVariable Long homeId,
            @Valid @RequestBody HomeRequestDto dto) {
        return ResponseEntity.ok(homeService.updateHome(homeId, dto));
    }

    @DeleteMapping("/{homeId}")
    public ResponseEntity<Void> deleteHome(@PathVariable Long homeId) {
        homeService.deleteHome(homeId);
        return ResponseEntity.noContent().build();
    }
}
