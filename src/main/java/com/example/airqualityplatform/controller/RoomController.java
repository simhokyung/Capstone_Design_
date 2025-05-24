package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.request.RoomRequestDto;
import com.example.airqualityplatform.dto.response.RoomResponseDto;
import com.example.airqualityplatform.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/homes/{homeId}/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponseDto> createRoom(
            @PathVariable Long homeId,
            @Valid @RequestBody RoomRequestDto dto) {
        RoomResponseDto created = roomService.createRoom(homeId, dto);
        URI location = URI.create(String.format("/homes/%d/rooms/%d", homeId, created.getRoomId()));
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getAllRooms(
            @PathVariable Long homeId) {
        return ResponseEntity.ok(roomService.getAllRoomsByHomeId(homeId));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDto> getRoomById(
            @PathVariable Long homeId,
            @PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponseDto> updateRoom(
            @PathVariable Long homeId,
            @PathVariable Long roomId,
            @Valid @RequestBody RoomRequestDto dto) {
        return ResponseEntity.ok(roomService.updateRoom(roomId, dto));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long homeId,
            @PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}
