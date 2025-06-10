package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.request.SensorRequestDto;
import com.example.airqualityplatform.dto.response.SensorResponseDto;
import com.example.airqualityplatform.service.SensorService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(SensorController.class)
public class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorService sensorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateSensor() throws Exception {
        SensorRequestDto requestDto = new SensorRequestDto();
        requestDto.setRoomId(1L);
        requestDto.setDeviceId(2L);
        requestDto.setSensorName("Test Sensor");
        requestDto.setSensorType("AirQuality");
        requestDto.setXCoordinate(10.0);
        requestDto.setYCoordinate(20.0);
        requestDto.setDescription("Test sensor description");

        SensorResponseDto responseDto = new SensorResponseDto();
        responseDto.setSensorId(50L);
        responseDto.setSensorName("Test Sensor");
        responseDto.setSensorType("AirQuality");
        responseDto.setXCoordinate(10.0);
        responseDto.setYCoordinate(20.0);
        responseDto.setDescription("Test sensor description");
        responseDto.setRegisteredAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(sensorService.createSensor(ArgumentMatchers.any(SensorRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value(50L))
                .andExpect(jsonPath("$.sensorName").value("Test Sensor"));
    }

    @Test
    public void testGetAllSensors() throws Exception {
        SensorResponseDto s1 = new SensorResponseDto();
        s1.setSensorId(50L);
        s1.setSensorName("Sensor One");
        SensorResponseDto s2 = new SensorResponseDto();
        s2.setSensorId(51L);
        s2.setSensorName("Sensor Two");

        when(sensorService.getAllSensors()).thenReturn(Arrays.asList(s1, s2));

        mockMvc.perform(get("/sensors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sensorId").value(50L))
                .andExpect(jsonPath("$[1].sensorId").value(51L));
    }

    @Test
    public void testGetSensorById() throws Exception {
        SensorResponseDto responseDto = new SensorResponseDto();
        responseDto.setSensorId(50L);
        responseDto.setSensorName("Sensor One");

        when(sensorService.getSensorById(ArgumentMatchers.eq(50L))).thenReturn(responseDto);

        mockMvc.perform(get("/sensors/{id}", 50L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value(50L))
                .andExpect(jsonPath("$.sensorName").value("Sensor One"));
    }

    @Test
    public void testUpdateSensor() throws Exception {
        SensorRequestDto updateDto = new SensorRequestDto();
        updateDto.setRoomId(1L);
        updateDto.setDeviceId(2L);
        updateDto.setSensorName("Updated Sensor");
        updateDto.setSensorType("Temperature");
        updateDto.setXCoordinate(15.0);
        updateDto.setYCoordinate(25.0);
        updateDto.setDescription("Updated description");

        SensorResponseDto responseDto = new SensorResponseDto();
        responseDto.setSensorId(50L);
        responseDto.setSensorName("Updated Sensor");
        responseDto.setSensorType("Temperature");
        responseDto.setXCoordinate(15.0);
        responseDto.setYCoordinate(25.0);
        responseDto.setDescription("Updated description");
        responseDto.setRegisteredAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(sensorService.updateSensor(ArgumentMatchers.eq(50L), ArgumentMatchers.any(SensorRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/sensors/{id}", 50L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorName").value("Updated Sensor"))
                .andExpect(jsonPath("$.sensorType").value("Temperature"));
    }

    @Test
    public void testDeleteSensor() throws Exception {
        // DELETE 요청은 보통 상태 코드 검증
        mockMvc.perform(delete("/sensors/{id}", 50L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
