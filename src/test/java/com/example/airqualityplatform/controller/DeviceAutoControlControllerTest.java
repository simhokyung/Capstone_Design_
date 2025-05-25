package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.request.DeviceAutoControlRequestDto;
import com.example.airqualityplatform.dto.response.DeviceAutoControlResponseDto;
import com.example.airqualityplatform.service.DeviceAutoControlService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(DeviceAutoControlController.class)
public class DeviceAutoControlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceAutoControlService deviceAutoControlService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateAutoControl() throws Exception {
        DeviceAutoControlRequestDto req = new DeviceAutoControlRequestDto();
        req.setPm25Threshold(35.0);
        req.setCo2Threshold(900.0);
        req.setActionPower(true);
        req.setActionMode("auto");
        req.setActionFanSpeed(3);

        DeviceAutoControlResponseDto responseDto = new DeviceAutoControlResponseDto();
        responseDto.setControlId(1L);
        responseDto.setPm25Threshold(35.0);
        responseDto.setCo2Threshold(900.0);
        responseDto.setActionPower(true);
        responseDto.setActionMode("auto");
        responseDto.setActionFanSpeed(3);
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(deviceAutoControlService.createAutoControl(ArgumentMatchers.any(DeviceAutoControlRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.controlId").value(1L))
                .andExpect(jsonPath("$.actionMode").value("auto"));
    }

    @Test
    public void testGetAllAutoControls() throws Exception {
        DeviceAutoControlResponseDto d1 = new DeviceAutoControlResponseDto();
        d1.setControlId(1L);
        DeviceAutoControlResponseDto d2 = new DeviceAutoControlResponseDto();
        d2.setControlId(2L);

        when(deviceAutoControlService.getAllAutoControls()).thenReturn(Arrays.asList(d1, d2));

        mockMvc.perform(get("/policies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].controlId").value(1L))
                .andExpect(jsonPath("$[1].controlId").value(2L));
    }

    @Test
    public void testGetAutoControlById() throws Exception {
        DeviceAutoControlResponseDto responseDto = new DeviceAutoControlResponseDto();
        responseDto.setControlId(1L);
        responseDto.setActionMode("auto");

        when(deviceAutoControlService.getAutoControlById(eq(1L)))
                .thenReturn(responseDto);

        mockMvc.perform(get("/policies/{controlId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.controlId").value(1L))
                .andExpect(jsonPath("$.actionMode").value("auto"));
    }

    @Test
    public void testUpdateAutoControl() throws Exception {
        DeviceAutoControlRequestDto req = new DeviceAutoControlRequestDto();
        req.setPm25Threshold(40.0);
        req.setCo2Threshold(950.0);
        req.setActionPower(false);
        req.setActionMode("manual");
        req.setActionFanSpeed(2);

        DeviceAutoControlResponseDto responseDto = new DeviceAutoControlResponseDto();
        responseDto.setControlId(1L);
        responseDto.setPm25Threshold(40.0);
        responseDto.setCo2Threshold(950.0);
        responseDto.setActionPower(false);
        responseDto.setActionMode("manual");
        responseDto.setActionFanSpeed(2);
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(deviceAutoControlService.updateAutoControl(eq(1L), ArgumentMatchers.any(DeviceAutoControlRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/policies/{controlId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actionMode").value("manual"))
                .andExpect(jsonPath("$.pm25Threshold").value(40.0));
    }

    @Test
    public void testDeleteAutoControl() throws Exception {
        // DELETE 요청은 단순 상태 코드 검증
        mockMvc.perform(delete("/policies/{controlId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
