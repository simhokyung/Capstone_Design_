package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.request.DeviceRequestDto;
import com.example.airqualityplatform.dto.response.DeviceResponseDto;
import com.example.airqualityplatform.service.DeviceService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(DeviceController.class)
public class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateDevice() throws Exception {
        DeviceRequestDto req = new DeviceRequestDto();
        req.setRoomId(1L);
        req.setDeviceName("Air Purifier");
        req.setDeviceType("air_purifier");
        req.setXCoordinate(50.0);
        req.setYCoordinate(75.0);
        req.setPower(true);
        req.setMode("auto");
        req.setFanSpeed(2);

        DeviceResponseDto responseDto = new DeviceResponseDto();
        responseDto.setDeviceId(100L);
        responseDto.setDeviceName("Air Purifier");
        responseDto.setDeviceType("air_purifier");
        responseDto.setXCoordinate(50.0);
        responseDto.setYCoordinate(75.0);
        responseDto.setPower(true);
        responseDto.setMode("auto");
        responseDto.setFanSpeed(2);
        responseDto.setRegisteredAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(deviceService.createDevice(any(DeviceRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(100L))
                .andExpect(jsonPath("$.deviceName").value("Air Purifier"));
    }

    @Test
    public void testGetAllDevices() throws Exception {
        DeviceResponseDto d1 = new DeviceResponseDto();
        d1.setDeviceId(100L);
        d1.setDeviceName("Device1");
        DeviceResponseDto d2 = new DeviceResponseDto();
        d2.setDeviceId(101L);
        d2.setDeviceName("Device2");

        when(deviceService.getAllDevices()).thenReturn(Arrays.asList(d1, d2));

        mockMvc.perform(get("/devices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deviceId").value(100L))
                .andExpect(jsonPath("$[1].deviceId").value(101L));
    }

    @Test
    public void testGetDeviceById() throws Exception {
        DeviceResponseDto responseDto = new DeviceResponseDto();
        responseDto.setDeviceId(100L);
        responseDto.setDeviceName("Air Purifier");

        when(deviceService.getDeviceById(eq(100L))).thenReturn(responseDto);

        mockMvc.perform(get("/devices/{deviceId}", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(100L))
                .andExpect(jsonPath("$.deviceName").value("Air Purifier"));
    }

    @Test
    public void testUpdateDevice() throws Exception {
        DeviceRequestDto req = new DeviceRequestDto();
        req.setRoomId(1L);
        req.setDeviceName("Updated Device");
        req.setDeviceType("air_purifier");
        req.setXCoordinate(60.0);
        req.setYCoordinate(80.0);
        req.setPower(true);
        req.setMode("auto");
        req.setFanSpeed(3);

        DeviceResponseDto responseDto = new DeviceResponseDto();
        responseDto.setDeviceId(100L);
        responseDto.setDeviceName("Updated Device");
        responseDto.setXCoordinate(60.0);
        responseDto.setMode("auto");

        when(deviceService.updateDevice(eq(100L), any(DeviceRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/devices/{deviceId}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(100L))
                .andExpect(jsonPath("$.deviceName").value("Updated Device"))
                .andExpect(jsonPath("$.xcoordinate").value(60.0));
    }

    @Test
    public void testDeleteDevice() throws Exception {
        // DELETE 요청 시 상태 코드만 확인
        mockMvc.perform(delete("/devices/{deviceId}", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
