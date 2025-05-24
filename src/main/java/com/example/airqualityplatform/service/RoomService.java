package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Home;
import com.example.airqualityplatform.domain.Room;
import com.example.airqualityplatform.dto.request.RoomRequestDto;
import com.example.airqualityplatform.dto.response.RoomResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.dto.mapper.RoomMapper;
import com.example.airqualityplatform.repository.HomeRepository;
import com.example.airqualityplatform.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HomeRepository homeRepository;

    @Transactional
    public RoomResponseDto createRoom(Long homeId, RoomRequestDto dto) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "홈을 찾을 수 없습니다. id: " + homeId));
        Room room = RoomMapper.toEntity(dto, null);
        room.setHome(home);
        Room saved = roomRepository.save(room);
        return RoomMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<RoomResponseDto> getAllRoomsByHomeId(Long homeId) {
        if (!homeRepository.existsById(homeId)) {
            throw new ResourceNotFoundException("홈을 찾을 수 없습니다. id: " + homeId);
        }
        return roomRepository.findByHome_HomeId(homeId).stream()
                .map(RoomMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomResponseDto getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "방을 찾을 수 없습니다. id: " + roomId));
        return RoomMapper.toResponseDto(room);
    }

    @Transactional
    public RoomResponseDto updateRoom(Long roomId, RoomRequestDto dto) {
        Room existing = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "방을 찾을 수 없습니다. id: " + roomId));
        RoomMapper.toEntity(dto, existing);
        Room saved = roomRepository.save(existing);
        return RoomMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("방을 찾을 수 없습니다. id: " + roomId);
        }
        roomRepository.deleteById(roomId);
    }
}