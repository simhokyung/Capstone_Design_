package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.request.MeasurementRequestDto;
import com.example.airqualityplatform.dto.response.MeasurementResponseDto;
import com.example.airqualityplatform.service.MeasurementService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(MeasurementController.class)
public class MeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeasurementService measurementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateMeasurement() throws Exception {
        MeasurementRequestDto req = new MeasurementRequestDto();
        req.setSensorId(10L);
        Date now = new Date();
        req.setTimestamp(now);
        req.setTemperature(23.5);
        req.setHumidity(45.0);
        req.setCo2(800.0);
        req.setVoc(0.12);
        req.setPm25(55.0);
        req.setPm10(70.0);
        req.setFanSpeed(2);

        MeasurementResponseDto responseDto = new MeasurementResponseDto();
        responseDto.setMeasurementId(100L);
        responseDto.setSensorId(10L);
        responseDto.setTemperature(23.5);
        responseDto.setHumidity(45.0);
        responseDto.setCo2(800.0);
        responseDto.setVoc(0.12);
        responseDto.setPm25(55.0);
        responseDto.setPm10(70.0);
        responseDto.setFanSpeed(2);
        responseDto.setTimestamp(now);

        when(measurementService.createMeasurement(ArgumentMatchers.any(MeasurementRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/measurements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.measurementId").value(100L))
                .andExpect(jsonPath("$.sensorId").value(10L))
                .andExpect(jsonPath("$.temperature").value(23.5));
    }

    @Test
    public void testGetAllMeasurements() throws Exception {
        MeasurementResponseDto m1 = new MeasurementResponseDto();
        m1.setMeasurementId(1L);
        m1.setTemperature(22.0);

        MeasurementResponseDto m2 = new MeasurementResponseDto();
        m2.setMeasurementId(2L);
        m2.setTemperature(24.0);

        when(measurementService.getAllMeasurements()).thenReturn(Arrays.asList(m1, m2));

        mockMvc.perform(get("/measurements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].measurementId").value(1L))
                .andExpect(jsonPath("$[1].measurementId").value(2L));
    }

    @Test
    public void testGetMeasurementById() throws Exception {
        MeasurementResponseDto responseDto = new MeasurementResponseDto();
        responseDto.setMeasurementId(100L);
        responseDto.setTemperature(22.0);
        responseDto.setSensorId(10L);

        when(measurementService.getMeasurementById(ArgumentMatchers.eq(100L))).thenReturn(responseDto);

        mockMvc.perform(get("/measurements/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.measurementId").value(100L))
                .andExpect(jsonPath("$.sensorId").value(10L));
    }

    @Test
    public void testDeleteMeasurement() throws Exception {
        mockMvc.perform(delete("/measurements/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
