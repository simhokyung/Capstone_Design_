package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.dto.request.RoomRequestDto;
import com.example.airqualityplatform.dto.response.RoomResponseDto;
import com.example.airqualityplatform.service.RoomService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateRoom() throws Exception {
        RoomRequestDto req = new RoomRequestDto();
        req.setRoomName("Living Room");
        req.setPolygon("[[0,0],[0,10],[10,10],[10,0],[0,0]]");
        req.setFloorNumber(1);
        req.setDescription("Spacious living room");

        RoomResponseDto responseDto = new RoomResponseDto();
        responseDto.setRoomId(10L);
        responseDto.setRoomName("Living Room");
        responseDto.setPolygon("[[0,0],[0,10],[10,10],[10,0],[0,0]]");
        responseDto.setFloorNumber(1);
        responseDto.setDescription("Spacious living room");
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(roomService.createRoom(ArgumentMatchers.eq(1L), any(RoomRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/homes/{homeId}/rooms", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(10L))
                .andExpect(jsonPath("$.roomName").value("Living Room"));
    }

    @Test
    public void testGetAllRooms() throws Exception {
        RoomResponseDto r1 = new RoomResponseDto();
        r1.setRoomId(10L);
        r1.setRoomName("Room 1");
        RoomResponseDto r2 = new RoomResponseDto();
        r2.setRoomId(11L);
        r2.setRoomName("Room 2");

        when(roomService.getAllRoomsByHomeId(ArgumentMatchers.eq(1L))).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/homes/{homeId}/rooms", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomId").value(10L))
                .andExpect(jsonPath("$[1].roomId").value(11L));
    }

    @Test
    public void testGetRoomById() throws Exception {
        RoomResponseDto roomResponse = new RoomResponseDto();
        roomResponse.setRoomId(10L);
        roomResponse.setRoomName("Office");

        when(roomService.getRoomById(ArgumentMatchers.eq(10L))).thenReturn(Optional.of(roomResponse));

        mockMvc.perform(get("/homes/{homeId}/rooms/{roomId}", 1L, 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(10L))
                .andExpect(jsonPath("$.roomName").value("Office"));
    }

    @Test
    public void testUpdateRoom() throws Exception {
        RoomRequestDto req = new RoomRequestDto();
        req.setRoomName("Updated Room");
        req.setPolygon("new_polygon");
        req.setFloorNumber(2);
        req.setDescription("New description");

        RoomResponseDto responseDto = new RoomResponseDto();
        responseDto.setRoomId(10L);
        responseDto.setRoomName("Updated Room");
        responseDto.setPolygon("new_polygon");
        responseDto.setFloorNumber(2);
        responseDto.setDescription("New description");
        responseDto.setCreatedAt(new Date());
        responseDto.setUpdatedAt(new Date());

        when(roomService.updateRoom(ArgumentMatchers.eq(10L), any(RoomRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/homes/{homeId}/rooms/{roomId}", 1L, 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomName").value("Updated Room"))
                .andExpect(jsonPath("$.polygon").value("new_polygon"));
    }

    @Test
    public void testDeleteRoom() throws Exception {
        // 보통 delete는 상태 코드만 확인
        // (삭제 시 반환하는 문자열이 있다면 검증할 수 있음)
        mockMvc.perform(delete("/homes/{homeId}/rooms/{roomId}", 1L, 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
