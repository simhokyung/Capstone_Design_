package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.Filter;
import com.example.airqualityplatform.dto.mapper.FilterMapper;
import com.example.airqualityplatform.dto.request.FilterRequestDto;
import com.example.airqualityplatform.dto.response.FilterResponseDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class FilterMapperTest {

    @Test
    public void testToEntity() {
        // FilterRequestDto 객체 생성 및 값 설정
        FilterRequestDto req = new FilterRequestDto();
        req.setFilterName("모델 X 필터");
        req.setRecommendedLifetime(100.0);

        // DTO를 엔티티로 변환 (신규 생성 시 null 전달)
        Filter filter = FilterMapper.toEntity(req, null);
        assertNotNull(filter);
        assertEquals("모델 X 필터", filter.getFilterName());
        assertEquals(100.0, filter.getRecommendedLifetime());
    }

    @Test
    public void testToResponseDto() {
        // Filter 엔티티 생성 및 값 설정
        Filter filter = new Filter();
        filter.setFilterId(50L);
        filter.setFilterName("모델 Y 필터");
        filter.setRecommendedLifetime(120.0);
        Date now = new Date();
        filter.setCreatedAt(now);
        filter.setUpdatedAt(now);

        FilterResponseDto dto = FilterMapper.toResponseDto(filter);
        assertNotNull(dto);
        assertEquals(50L, dto.getFilterId());
        assertEquals("모델 Y 필터", dto.getFilterName());
        assertEquals(120.0, dto.getRecommendedLifetime());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
