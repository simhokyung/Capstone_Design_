package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.request.FilterRequestDto;
import com.example.airqualityplatform.dto.response.FilterResponseDto;
import com.example.airqualityplatform.service.FilterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(controllers = FilterController.class)
public class FilterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.example.airqualityplatform.service.FilterService filterService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateFilter() throws Exception {
        FilterRequestDto req = new FilterRequestDto();
        req.setFilterName("Model X Filter");
        req.setRecommendedLifetime(100.0);

        FilterResponseDto responseDto = new FilterResponseDto();
        responseDto.setFilterId(10L);
        responseDto.setFilterName("Model X Filter");
        responseDto.setRecommendedLifetime(100.0);
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(filterService.createFilter(ArgumentMatchers.any(FilterRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/filters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filterId").value(10L))
                .andExpect(jsonPath("$.filterName").value("Model X Filter"));
    }

    @Test
    public void testGetAllFilters() throws Exception {
        FilterResponseDto f1 = new FilterResponseDto();
        f1.setFilterId(10L);
        FilterResponseDto f2 = new FilterResponseDto();
        f2.setFilterId(11L);

        when(filterService.getAllFilters()).thenReturn(Arrays.asList(f1, f2));

        mockMvc.perform(get("/filters")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filterId").value(10L))
                .andExpect(jsonPath("$[1].filterId").value(11L));
    }

    @Test
    public void testGetFilterById() throws Exception {
        FilterResponseDto responseDto = new FilterResponseDto();
        responseDto.setFilterId(10L);
        responseDto.setFilterName("Model Y Filter");

        when(filterService.getFilterById(eq(10L))).thenReturn(responseDto);

        mockMvc.perform(get("/filters/{filterId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filterId").value(10L))
                .andExpect(jsonPath("$.filterName").value("Model Y Filter"));
    }

    @Test
    public void testUpdateFilter() throws Exception {
        FilterRequestDto req = new FilterRequestDto();
        req.setFilterName("Updated Filter");
        req.setRecommendedLifetime(120.0);

        FilterResponseDto responseDto = new FilterResponseDto();
        responseDto.setFilterId(10L);
        responseDto.setFilterName("Updated Filter");
        responseDto.setRecommendedLifetime(120.0);
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(filterService.updateFilter(eq(10L), ArgumentMatchers.any(FilterRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/filters/{filterId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filterName").value("Updated Filter"))
                .andExpect(jsonPath("$.recommendedLifetime").value(120.0));
    }

    @Test
    public void testDeleteFilter() throws Exception {
        // delete 는 상태 코드 검증 (예: 200 OK)
        mockMvc.perform(delete("/filters/{filterId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
