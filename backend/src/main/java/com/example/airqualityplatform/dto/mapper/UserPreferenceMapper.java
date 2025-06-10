package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.UserPreference;
import com.example.airqualityplatform.dto.request.UserPreferenceRequestDto;
import com.example.airqualityplatform.dto.response.UserPreferenceResponseDto;

public class UserPreferenceMapper {
    public static UserPreference toEntity(UserPreferenceRequestDto dto, UserPreference entity) {
        if (entity == null) entity = new UserPreference();
        entity.setPm25Threshold(dto.getPm25Threshold());
        entity.setPm100Threshold(dto.getPm100Threshold());
        entity.setCo2Threshold(dto.getCo2Threshold());
        entity.setVocThreshold(dto.getVocThreshold());
        return entity;
    }

    public static UserPreferenceResponseDto toResponseDto(UserPreference entity) {
        UserPreferenceResponseDto dto = new UserPreferenceResponseDto();
        dto.setPreferenceId(entity.getPreferenceId());
        dto.setUserId(entity.getUser().getUserId());
        dto.setPm25Threshold(entity.getPm25Threshold());
        dto.setPm100Threshold(entity.getPm100Threshold());
        dto.setCo2Threshold(entity.getCo2Threshold());
        dto.setVocThreshold(entity.getVocThreshold());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}