package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.response.HeatmapDataResponse;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(HeatmapController.class)
public class HeatmapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HeatmapDataService heatmapDataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetHeatmapData() throws Exception {
        // 두 개의 HeatmapDataResponse DTO 생성
        HeatmapDataResponse dto1 = new HeatmapDataResponse();
        dto1.setSensorId(10L);
        dto1.setXCoordinate(55.0);
        dto1.setYCoordinate(75.0);
        dto1.setPm25(50.0);
        dto1.setTimestamp(new Date(1600000000000L));

        HeatmapDataResponse dto2 = new HeatmapDataResponse();
        dto2.setSensorId(11L);
        dto2.setXCoordinate(65.0);
        dto2.setYCoordinate(85.0);
        dto2.setPm25(60.0);
        dto2.setTimestamp(new Date(1600003600000L));

        when(heatmapDataService.getHeatmapDataByRoom(ArgumentMatchers.eq(1L)))
                .thenReturn(Arrays.asList(dto1, dto2));

        mockMvc.perform(get("/heatmap")
                        .param("roomId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sensorId").value(10L))
                .andExpect(jsonPath("$[1].sensorId").value(11L));
    }
}
