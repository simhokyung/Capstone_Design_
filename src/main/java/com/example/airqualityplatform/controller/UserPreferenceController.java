package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.UserPreferenceRequestDto;
import com.example.airqualityplatform.dto.response.UserPreferenceResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.service.UserPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/preferences")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService preferenceService;

    /**
     * GET /users/{userId}/preferences : 사용자 기준치 조회 (기존 또는 추천값)
     */
    @GetMapping
    public ResponseEntity<UserPreferenceResponseDto> getUserPreference(
            @PathVariable Long userId) {
        UserPreferenceResponseDto dto = preferenceService.getPreferenceByUserId(userId);
        return ResponseEntity.ok(dto);
    }

    /**
     * POST /users/{userId}/preferences : 사용자 기준치 생성/수정
     */
    @PostMapping
    public ResponseEntity<UserPreferenceResponseDto> createOrUpdatePreference(
            @PathVariable Long userId,
            @Valid @RequestBody UserPreferenceRequestDto dto) {
        UserPreferenceResponseDto result = preferenceService.createOrUpdatePreference(userId, dto);
        return ResponseEntity.ok(result);
    }
}