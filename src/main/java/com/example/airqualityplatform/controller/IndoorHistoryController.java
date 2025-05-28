package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.response.IndoorRoomHistoryDto;
import com.example.airqualityplatform.service.IndoorHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/indoor/rooms")
@RequiredArgsConstructor
public class IndoorHistoryController {

    private final IndoorHistoryService service;

    @GetMapping("/{roomId}/history")
    public ResponseEntity<IndoorRoomHistoryDto> getRoomHistory(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(defaultValue = "15") int intervalMinutes
    ) throws Exception {
        return ResponseEntity.ok(
                service.getRoomHistory(roomId, start, end, intervalMinutes)
        );
    }
}