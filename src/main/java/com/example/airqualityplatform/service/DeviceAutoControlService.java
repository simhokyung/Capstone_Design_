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
import org.springframework.transaction.annotation.Propagation;
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
        DeviceAutoControl policy = DeviceAutoControlMapper.toEntity(dto, null);
        DeviceAutoControl saved = controlRepo.save(policy);

        List<Device> devices = deviceRepo.findByRoom_RoomId(dto.getRoomId());
        if (devices.isEmpty()) {
            throw new ResourceNotFoundException("해당 방에 등록된 기기가 없습니다. roomId=" + dto.getRoomId());
        }
        devices.forEach(d -> d.setPolicy(saved));
        deviceRepo.saveAll(devices);
        saved.setDevices(devices);

        // AI 전송은 별도 트랜잭션으로 격리
        aiPolicyService.sendPolicyToAiAsync(saved.getControlId());
        return DeviceAutoControlMapper.toResponseDto(saved);
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
                .orElseThrow(() -> new ResourceNotFoundException("자동제어 정책이 없습니다. id=" + controlId));
        return DeviceAutoControlMapper.toResponseDto(entity);
    }

    @Transactional
    public DeviceAutoControlResponseDto updateAutoControl(Long controlId, DeviceAutoControlRequestDto dto) {
        DeviceAutoControl existing = controlRepo.findById(controlId)
                .orElseThrow(() -> new ResourceNotFoundException("자동제어 정책이 없습니다. id=" + controlId));
        DeviceAutoControlMapper.toEntity(dto, existing);
        DeviceAutoControl saved = controlRepo.save(existing);

        aiPolicyService.sendPolicyToAiAsync(saved.getControlId());
        return DeviceAutoControlMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteAutoControl(Long controlId) {
        if (!controlRepo.existsById(controlId)) {
            throw new ResourceNotFoundException("자동제어 정책이 없습니다. id=" + controlId);
        }
        controlRepo.deleteById(controlId);
    }
}
