package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.Home;
import com.example.airqualityplatform.dto.mapper.HomeMapper;
import com.example.airqualityplatform.dto.request.HomeRequestDto;
import com.example.airqualityplatform.dto.response.HomeResponseDto;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class HomeMapperTest {

    @Test
    public void testToEntity() {
        // HomeRequestDto 객체 생성
        HomeRequestDto req = new HomeRequestDto();
        req.setHomeName("우리집");
        req.setAddress("서울특별시 강남구 테헤란로 123");

        // DTO를 엔티티로 변환 (업데이트 시 null 전달)
        Home home = HomeMapper.toEntity(req, null);

        assertNotNull(home);
        assertEquals("우리집", home.getHomeName());
        assertEquals("서울특별시 강남구 테헤란로 123", home.getAddress());
        // createdAt과 updatedAt는 @PrePersist에 의해 설정되므로 테스트에서는 생략 또는 별도 설정 필요
    }

    @Test
    public void testToResponseDto() {
        // Home 엔티티 생성 및 필드 설정
        Home home = new Home();
        home.setHomeId(1L);
        home.setHomeName("우리집");
        home.setAddress("서울특별시 강남구 테헤란로 123");
        Date now = new Date();
        home.setCreatedAt(now);
        home.setUpdatedAt(now);

        HomeResponseDto dto = HomeMapper.toResponseDto(home);

        assertNotNull(dto);
        assertEquals(1L, dto.getHomeId());
        assertEquals("우리집", dto.getHomeName());
        assertEquals("서울특별시 강남구 테헤란로 123", dto.getAddress());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
