// src/main/java/com/example/airqualityplatform/service/DeviceAutoControlService.java
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceAutoControlService {

    private final DeviceAutoControlRepository controlRepo;
    private final DeviceRepository deviceRepo;
    private final AiPolicyService aiPolicyService;

    @Transactional
    public DeviceAutoControlResponseDto createAutoControl(DeviceAutoControlRequestDto dto) {
        // 1) save policy
        DeviceAutoControl policy = DeviceAutoControlMapper.toEntity(dto, null);
        DeviceAutoControl saved = controlRepo.save(policy);

        // 2) attach devices by room
        List<Device> devices = deviceRepo.findByRoom_RoomId(dto.getRoomId());
        if (devices.isEmpty()) {
            throw new ResourceNotFoundException("해당 방에 등록된 기기가 없습니다. roomId=" + dto.getRoomId());
        }
        devices.forEach(d -> d.setPolicy(saved));
        deviceRepo.saveAll(devices);
        saved.setDevices(devices);

        // 3) fire off AI call asynchronously
        aiPolicyService.sendPolicyToAiAsync(saved.getControlId());

        // 4) return REST response
        return DeviceAutoControlMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DeviceAutoControlResponseDto> getAllAutoControls() {
        return controlRepo.findAll().stream()
                .map(DeviceAutoControlMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeviceAutoControlResponseDto getAutoControlById(Long id) {
        DeviceAutoControl e = controlRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("자동제어 정책이 없습니다. id=" + id));
        return DeviceAutoControlMapper.toResponseDto(e);
    }

    @Transactional
    public DeviceAutoControlResponseDto updateAutoControl(Long id, DeviceAutoControlRequestDto dto) {
        DeviceAutoControl existing = controlRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("자동제어 정책이 없습니다. id=" + id));
        DeviceAutoControlMapper.toEntity(dto, existing);
        DeviceAutoControl saved = controlRepo.save(existing);
        aiPolicyService.sendPolicyToAiAsync(saved.getControlId());
        return DeviceAutoControlMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteAutoControl(Long id) {
        if (!controlRepo.existsById(id)) {
            throw new ResourceNotFoundException("자동제어 정책이 없습니다. id=" + id);
        }
        controlRepo.deleteById(id);
    }
}
