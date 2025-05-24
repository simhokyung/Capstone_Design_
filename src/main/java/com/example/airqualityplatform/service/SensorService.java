package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Room;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.request.SensorRequestDto;
import com.example.airqualityplatform.dto.response.SensorResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.dto.mapper.SensorMapper;
import com.example.airqualityplatform.repository.RoomRepository;
import com.example.airqualityplatform.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public SensorResponseDto createSensor(SensorRequestDto dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 방을 찾을 수 없습니다. id: " + dto.getRoomId()));
        Sensor sensor = SensorMapper.toEntity(dto, null);
        sensor.setRoom(room);
        Sensor saved = sensorRepository.save(sensor);
        return SensorMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<SensorResponseDto> getAllSensors() {
        return sensorRepository.findAll().stream()
                .map(SensorMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SensorResponseDto getSensorById(Long id) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "센서를 찾을 수 없습니다. id: " + id));
        return SensorMapper.toResponseDto(sensor);
    }

    @Transactional(readOnly = true)
    public SensorResponseDto getByExternalSensorId(Long externalId) {
        Sensor sensor = sensorRepository.findBySensorId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "센서를 찾을 수 없습니다. sensorId: " + externalId));
        return SensorMapper.toResponseDto(sensor);
    }

    @Transactional
    public SensorResponseDto updateSensor(Long id, SensorRequestDto dto) {
        Sensor existing = sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "센서를 찾을 수 없습니다. id: " + id));
        SensorMapper.toEntity(dto, existing);
        if (dto.getRoomId() != null) {
            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "해당 방을 찾을 수 없습니다. id: " + dto.getRoomId()));
            existing.setRoom(room);
        }
        Sensor saved = sensorRepository.save(existing);
        return SensorMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteSensor(Long id) {
        if (!sensorRepository.existsById(id)) {
            throw new ResourceNotFoundException("센서를 찾을 수 없습니다. id: " + id);
        }
        sensorRepository.deleteById(id);
    }
}