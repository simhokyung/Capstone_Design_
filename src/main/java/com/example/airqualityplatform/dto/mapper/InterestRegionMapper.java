package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.InterestRegion;
import com.example.airqualityplatform.dto.response.InterestRegionResponseDto;

public class InterestRegionMapper {
    public static InterestRegionResponseDto toResponseDto(InterestRegion e) {
        InterestRegionResponseDto dto = new InterestRegionResponseDto();
        dto.setId(e.getId());
        dto.setUserId(e.getUser().getUserId());
        dto.setRegionName(e.getRegionName());
        dto.setLatitude(e.getLatitude());
        dto.setLongitude(e.getLongitude());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }
}