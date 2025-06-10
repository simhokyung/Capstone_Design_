package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.domain.UserPreference;
import com.example.airqualityplatform.dto.mapper.UserPreferenceMapper;
import com.example.airqualityplatform.dto.request.UserPreferenceRequestDto;
import com.example.airqualityplatform.dto.response.UserPreferenceResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.UserPreferenceRepository;
import com.example.airqualityplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository preferenceRepository;

    /**
     * 사용자 기준치 조회: 기존 설정이 있으면 반환,
     * 없으면 건강 상태(천식/알레르기/일반) 기반 추천값 생성
     */
    @Transactional(readOnly = true)
    public UserPreferenceResponseDto getPreferenceByUserId(Long userId) {
        Optional<UserPreference> existing = preferenceRepository.findByUser_UserId(userId);
        if (existing.isPresent()) {
            return UserPreferenceMapper.toResponseDto(existing.get());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 사용자를 찾을 수 없습니다. id: " + userId));

        // 새로운 추천 기준치 설정
        double pm25;
        double pm10;
        double co2 = 1000.0;
        double voc;

        if (user.isHasAsthma()) {
            // 천식 환자
            pm25 = 5.0;
            pm10 = 15.0;
            voc  = 400.0;
        } else if (user.isHasAllergy()) {
            // 알레르기 환자
            pm25 = 10.0;
            pm10 = 25.0;
            voc  = 40.0;
        } else {
            // 일반 사용자
            pm25 = 15.0;
            pm10 = 30.0;
            voc  = 400.0;
        }

        UserPreferenceResponseDto dto = new UserPreferenceResponseDto();
        dto.setPreferenceId(null);
        dto.setUserId(userId);
        dto.setPm25Threshold(pm25);
        dto.setPm100Threshold(pm10);
        dto.setCo2Threshold(co2);
        dto.setVocThreshold(voc);
        dto.setCreatedAt(null);
        dto.setUpdatedAt(null);
        return dto;
    }

    /**
     * 사용자 기준치 생성 또는 업데이트
     */
    @Transactional
    public UserPreferenceResponseDto createOrUpdatePreference(Long userId,
                                                              UserPreferenceRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 사용자를 찾을 수 없습니다. id: " + userId));
        UserPreference pref = preferenceRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    UserPreference p = new UserPreference();
                    p.setUser(user);
                    return p;
                });
        UserPreferenceMapper.toEntity(dto, pref);
        pref.setUser(user);
        UserPreference saved = preferenceRepository.save(pref);
        return UserPreferenceMapper.toResponseDto(saved);
    }
}