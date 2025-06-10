package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Filter;
import com.example.airqualityplatform.dto.mapper.FilterMapper;
import com.example.airqualityplatform.dto.request.FilterRequestDto;
import com.example.airqualityplatform.dto.response.FilterResponseDto;
import com.example.airqualityplatform.repository.FilterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FilterServiceTest {

    @InjectMocks
    private FilterService filterService;

    @Mock
    private FilterRepository filterRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateFilter() {
        FilterRequestDto req = new FilterRequestDto();
        req.setFilterName("Model X Filter");
        req.setRecommendedLifetime(100.0);

        Filter entity = FilterMapper.toEntity(req, null);
        entity.setFilterId(10L);
        Date now = new Date();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        when(filterRepository.save(any(Filter.class))).thenReturn(entity);

        FilterResponseDto response = filterService.createFilter(req);
        assertNotNull(response);
        assertEquals(10L, response.getFilterId());
        assertEquals("Model X Filter", response.getFilterName());
        verify(filterRepository, times(1)).save(any(Filter.class));
    }

    @Test
    public void testGetAllFilters() {
        Filter f1 = new Filter();
        f1.setFilterId(10L);
        Filter f2 = new Filter();
        f2.setFilterId(11L);

        when(filterRepository.findAll()).thenReturn(Arrays.asList(f1, f2));
        assertEquals(2, filterService.getAllFilters().size());
        verify(filterRepository, times(1)).findAll();
    }

    @Test
    public void testGetFilterById() {
        // 테스트용 Filter 엔티티 생성
        Filter filter = new Filter();
        filter.setFilterId(20L);
        filter.setFilterName("Test Filter");

        // 모킹: 필터 ID로 검색 시, 생성한 Filter 엔티티를 Optional.of(filter)로 반환
        when(filterRepository.findById(20L)).thenReturn(Optional.of(filter));

        // 실제 서비스 메서드 호출: 반환 타입이 FilterResponseDto임
        FilterResponseDto result = filterService.getFilterById(20L);

        // 반환된 DTO 검증
        assertNotNull(result);
        assertEquals("Test Filter", result.getFilterName());
        verify(filterRepository, times(1)).findById(20L);
    }

    @Test
    public void testUpdateFilter() {
        // 가정: FilterService.updateFilter(...) → FilterResponseDto
        Long filterId = 10L;
        Filter existing = new Filter();
        existing.setFilterId(filterId);
        existing.setFilterName("Old Filter");
        existing.setRecommendedLifetime(80.0);

        FilterRequestDto updateReq = new FilterRequestDto();
        updateReq.setFilterName("Updated Filter");
        updateReq.setRecommendedLifetime(120.0);

        when(filterRepository.findById(filterId)).thenReturn(Optional.of(existing));
        when(filterRepository.save(any(Filter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FilterResponseDto result = filterService.updateFilter(filterId, updateReq);
        assertEquals("Updated Filter", result.getFilterName());
        assertEquals(120.0, result.getRecommendedLifetime());
    }

    @Test
    public void testDeleteFilter() {
        Long filterId = 10L;
        doNothing().when(filterRepository).deleteById(filterId);
        filterService.deleteFilter(filterId);
        verify(filterRepository, times(1)).deleteById(filterId);
    }
}
