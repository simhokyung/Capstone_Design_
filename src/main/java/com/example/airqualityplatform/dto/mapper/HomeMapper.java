package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.Home;
import com.example.airqualityplatform.dto.request.HomeRequestDto;
import com.example.airqualityplatform.dto.response.HomeResponseDto;

public class HomeMapper {

    public static Home toEntity(HomeRequestDto dto, Home home) {
        if (home == null) {
            home = new Home();
        }
        home.setHomeName(dto.getHomeName());
        home.setAddress(dto.getAddress());
        return home;
    }

    public static HomeResponseDto toResponseDto(Home home) {
        HomeResponseDto dto = new HomeResponseDto();
        dto.setHomeId(home.getHomeId());
        dto.setHomeName(home.getHomeName());
        dto.setAddress(home.getAddress());
        dto.setCreatedAt(home.getCreatedAt());
        dto.setUpdatedAt(home.getUpdatedAt());
        return dto;
    }
}