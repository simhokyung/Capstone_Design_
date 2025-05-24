package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.DeviceAutoControl;
import com.example.airqualityplatform.dto.mapper.DeviceAutoControlMapper;
import com.example.airqualityplatform.dto.request.DeviceAutoControlRequestDto;
import com.example.airqualityplatform.dto.response.DeviceAutoControlResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.DeviceAutoControlRepository;
import com.example.airqualityplatform.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceAutoControlService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceAutoControlService.class);
    private final DeviceAutoControlRepository controlRepo;
    private final DeviceRepository deviceRepo;

    /**
     * 사용자 선택 Room 기반으로 정책 생성 및 적용
     */
    @Transactional
    public DeviceAutoControlResponseDto createAutoControl(DeviceAutoControlRequestDto dto) {
        // 정책 생성
        DeviceAutoControl policy = DeviceAutoControlMapper.toEntity(dto, null);
        DeviceAutoControl savedPolicy = controlRepo.save(policy);
        logger.debug("Created policy id={}", savedPolicy.getControlId());

        // 해당 Room의 모든 Device에 정책 적용
        List<Device> devices = deviceRepo.findByRoom_RoomId(dto.getRoomId());
        if (devices.isEmpty()) {
            throw new ResourceNotFoundException("해당 방에 등록된 기기가 없습니다. roomId: " + dto.getRoomId());
        }
        for (Device device : devices) {
            device.setPolicy(savedPolicy);
        }
        deviceRepo.saveAll(devices);
        savedPolicy.setDevices(devices);

        return DeviceAutoControlMapper.toResponseDto(savedPolicy);
    }

    @Transactional(readOnly = true)
    public List<DeviceAutoControlResponseDto> getAllAutoControls() {
        return controlRepo.findAll().stream()
                .map(DeviceAutoControlMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeviceAutoControlResponseDto getAutoControlById(Long controlId) {
        DeviceAutoControl entity = controlRepo.findById(controlId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 자동제어 정책을 찾을 수 없습니다. id: " + controlId));
        return DeviceAutoControlMapper.toResponseDto(entity);
    }

    @Transactional
    public DeviceAutoControlResponseDto updateAutoControl(Long controlId, DeviceAutoControlRequestDto dto) {
        DeviceAutoControl existing = controlRepo.findById(controlId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 자동제어 정책을 찾을 수 없습니다. id: " + controlId));
        DeviceAutoControlMapper.toEntity(dto, existing);
        DeviceAutoControl saved = controlRepo.save(existing);
        logger.debug("Updated policy id={}", saved.getControlId());
        return DeviceAutoControlMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteAutoControl(Long controlId) {
        if (!controlRepo.existsById(controlId)) {
            throw new ResourceNotFoundException(
                    "해당 자동제어 정책을 찾을 수 없습니다. id: " + controlId);
        }
        controlRepo.deleteById(controlId);
        logger.debug("Deleted policy id={}", controlId);
    }
}