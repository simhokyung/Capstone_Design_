// src/main/java/com/example/airqualityplatform/controller/FilterController.java
package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.FilterRequestDto;
import com.example.airqualityplatform.dto.response.FilterResponseDto;
import com.example.airqualityplatform.service.FilterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/filters")
@RequiredArgsConstructor
public class FilterController {

    private final FilterService filterService;

    @PostMapping
    public ResponseEntity<FilterResponseDto> createFilter(
            @Valid @RequestBody FilterRequestDto dto) {
        FilterResponseDto created = filterService.createFilter(dto);
        URI location = URI.create("/filters/" + created.getFilterId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<FilterResponseDto>> getAllFilters() {
        return ResponseEntity.ok(filterService.getAllFilters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilterResponseDto> getFilterById(@PathVariable Long id) {
        return ResponseEntity.ok(filterService.getFilterById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilterResponseDto> updateFilter(
            @PathVariable Long id,
            @Valid @RequestBody FilterRequestDto dto) {
        return ResponseEntity.ok(filterService.updateFilter(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilter(@PathVariable Long id) {
        filterService.deleteFilter(id);
        return ResponseEntity.noContent().build();
    }
}
