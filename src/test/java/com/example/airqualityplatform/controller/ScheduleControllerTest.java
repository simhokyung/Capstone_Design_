package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.request.ScheduleRequestDto;
import com.example.airqualityplatform.dto.response.ScheduleResponseDto;
import com.example.airqualityplatform.service.ScheduleService;
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
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateSchedule() throws Exception {
        ScheduleRequestDto req = new ScheduleRequestDto();
        req.setDeviceId(20L);
        req.setUserId(15L);
        req.setStartTime("07:00");
        req.setEndTime("09:00");
        req.setDaysOfWeek("MON,TUE,WED");

        ScheduleResponseDto responseDto = new ScheduleResponseDto();
        responseDto.setScheduleId(30L);
        responseDto.setDeviceId(20L);
        responseDto.setUserId(15L);
        responseDto.setStartTime("07:00");
        responseDto.setEndTime("09:00");
        responseDto.setDaysOfWeek("MON,TUE,WED");
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(scheduleService.createSchedule(ArgumentMatchers.any(ScheduleRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(30L))
                .andExpect(jsonPath("$.startTime").value("07:00"));
    }

    @Test
    public void testGetAllSchedules() throws Exception {
        ScheduleResponseDto s1 = new ScheduleResponseDto();
        s1.setScheduleId(30L);
        s1.setStartTime("07:00");
        s1.setEndTime("09:00");
        s1.setDaysOfWeek("MON,TUE,WED");

        ScheduleResponseDto s2 = new ScheduleResponseDto();
        s2.setScheduleId(31L);
        s2.setStartTime("10:00");
        s2.setEndTime("12:00");
        s2.setDaysOfWeek("THU,FRI");

        when(scheduleService.getAllSchedules()).thenReturn(Arrays.asList(s1, s2));

        mockMvc.perform(get("/schedules")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].scheduleId").value(30L))
                .andExpect(jsonPath("$[1].scheduleId").value(31L));
    }

    @Test
    public void testGetScheduleById() throws Exception {
        ScheduleResponseDto responseDto = new ScheduleResponseDto();
        responseDto.setScheduleId(30L);
        responseDto.setStartTime("07:00");
        responseDto.setEndTime("09:00");
        responseDto.setDaysOfWeek("MON,TUE,WED");
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(scheduleService.getScheduleById(ArgumentMatchers.eq(30L)))
                .thenReturn(responseDto);

        mockMvc.perform(get("/schedules/{id}", 30L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(30L))
                .andExpect(jsonPath("$.startTime").value("07:00"));
    }

    @Test
    public void testDeleteSchedule() throws Exception {
        // delete 시 반환하는 값이 없으면 상태 코드만 검증
        mockMvc.perform(delete("/schedules/{id}", 30L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
