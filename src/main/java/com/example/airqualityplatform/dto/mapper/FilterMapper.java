// src/main/java/com/example/airqualityplatform/dto/mapper/FilterMapper.java
package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.Filter;
import com.example.airqualityplatform.dto.request.FilterRequestDto;
import com.example.airqualityplatform.dto.response.FilterResponseDto;

public class FilterMapper {

    public static Filter toEntity(FilterRequestDto dto, Filter entity) {
        if (entity == null) {
            entity = new Filter();
        }
        entity.setFilterName(dto.getFilterName());
        return entity;
    }

    public static FilterResponseDto toResponseDto(Filter entity) {
        FilterResponseDto dto = new FilterResponseDto();
        dto.setFilterId(entity.getFilterId());
        dto.setFilterName(entity.getFilterName());
        dto.setDeviceId(entity.getDevice().getDeviceId());
        dto.setSensorId(entity.getSensor().getSensorId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
