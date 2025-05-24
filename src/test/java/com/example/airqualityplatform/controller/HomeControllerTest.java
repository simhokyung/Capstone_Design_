package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.response.HomeResponseDto;
import com.example.airqualityplatform.dto.request.HomeRequestDto;
import com.example.airqualityplatform.service.HomeService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeService homeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateHome() throws Exception {
        HomeRequestDto requestDto = new HomeRequestDto();
        requestDto.setHomeName("Test Home");
        requestDto.setAddress("123 Test Street");

        HomeResponseDto responseDto = new HomeResponseDto();
        responseDto.setHomeId(1L);
        responseDto.setHomeName("Test Home");
        responseDto.setAddress("123 Test Street");
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(homeService.createHome(any(HomeRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/homes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeId").value(1L))
                .andExpect(jsonPath("$.homeName").value("Test Home"))
                .andExpect(jsonPath("$.address").value("123 Test Street"));
    }

    @Test
    public void testGetAllHomes() throws Exception {
        HomeResponseDto h1 = new HomeResponseDto();
        h1.setHomeId(1L);
        h1.setHomeName("Home1");
        h1.setAddress("Address1");
        h1.setCreatedAt(new Date());
        h1.setUpdatedAt(new Date());

        HomeResponseDto h2 = new HomeResponseDto();
        h2.setHomeId(2L);
        h2.setHomeName("Home2");
        h2.setAddress("Address2");
        h2.setCreatedAt(new Date());
        h2.setUpdatedAt(new Date());

        when(homeService.getAllHomes()).thenReturn(Arrays.asList(h1, h2));

        mockMvc.perform(get("/homes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].homeId").value(1L))
                .andExpect(jsonPath("$[1].homeId").value(2L));
    }

    @Test
    public void testGetHomeById() throws Exception {
        HomeResponseDto responseDto = new HomeResponseDto();
        responseDto.setHomeId(1L);
        responseDto.setHomeName("Home1");
        responseDto.setAddress("Address1");
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(homeService.getHomeById(1L)).thenReturn(java.util.Optional.of(responseDto));

        mockMvc.perform(get("/homes/{homeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeId").value(1L))
                .andExpect(jsonPath("$.homeName").value("Home1"))
                .andExpect(jsonPath("$.address").value("Address1"));
    }

    @Test
    public void testUpdateHome() throws Exception {
        HomeRequestDto updateDto = new HomeRequestDto();
        updateDto.setHomeName("Updated Home");
        updateDto.setAddress("Updated Address");

        HomeResponseDto responseDto = new HomeResponseDto();
        responseDto.setHomeId(1L);
        responseDto.setHomeName("Updated Home");
        responseDto.setAddress("Updated Address");
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(homeService.updateHome(eq(1L), any(HomeRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/homes/{homeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeId").value(1L))
                .andExpect(jsonPath("$.homeName").value("Updated Home"))
                .andExpect(jsonPath("$.address").value("Updated Address"));
    }

    @Test
    public void testDeleteHome() throws Exception {
        // Mockito에서 void 메서드를 모킹할 때는 doNothing(), doThrow() 등을 사용
        doNothing().when(homeService).deleteHome(eq(1L));

        mockMvc.perform(delete("/homes/{homeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());  // 상태 코드만 검증
        // 추가 검증: 반환 메시지 검증 등 (필요 시)
    }
}
