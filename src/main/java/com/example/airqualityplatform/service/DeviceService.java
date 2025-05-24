package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.Room;
import com.example.airqualityplatform.dto.request.DeviceRequestDto;
import com.example.airqualityplatform.dto.response.DeviceResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.dto.mapper.DeviceMapper;
import com.example.airqualityplatform.repository.DeviceRepository;
import com.example.airqualityplatform.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public DeviceResponseDto createDevice(DeviceRequestDto dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 방을 찾을 수 없습니다. id: " + dto.getRoomId()));
        Device device = DeviceMapper.toEntity(dto, null, room);
        Device saved = deviceRepository.save(device);
        return DeviceMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DeviceResponseDto> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(DeviceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeviceResponseDto getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "장치를 찾을 수 없습니다. id: " + id));
        return DeviceMapper.toResponseDto(device);
    }

    @Transactional(readOnly = true)
    public DeviceResponseDto getByExternalDeviceId(String externalId) {
        Device device = deviceRepository.findByDeviceId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "장치를 찾을 수 없습니다. deviceId: " + externalId));
        return DeviceMapper.toResponseDto(device);
    }

    @Transactional
    public DeviceResponseDto updateDevice(Long id, DeviceRequestDto dto) {
        Device existing = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "장치를 찾을 수 없습니다. id: " + id));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 방을 찾을 수 없습니다. id: " + dto.getRoomId()));
        Device updated = DeviceMapper.toEntity(dto, existing, room);
        Device saved = deviceRepository.save(updated);
        return DeviceMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("장치를 찾을 수 없습니다. id: " + id);
        }
        deviceRepository.deleteById(id);
    }
}
