package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.DeviceAutoControl;
import com.example.airqualityplatform.dto.mapper.DeviceAutoControlMapper;
import com.example.airqualityplatform.dto.request.DeviceAutoControlRequestDto;
import com.example.airqualityplatform.dto.response.DeviceAutoControlResponseDto;
import com.example.airqualityplatform.event.AiPolicySendEvent;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.DeviceAutoControlRepository;
import com.example.airqualityplatform.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceAutoControlService {

    private final DeviceAutoControlRepository controlRepo;
    private final DeviceRepository deviceRepo;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public DeviceAutoControlResponseDto createAutoControl(DeviceAutoControlRequestDto dto) {
        // 1) 정책 저장
        DeviceAutoControl policy = DeviceAutoControlMapper.toEntity(dto, null);
        DeviceAutoControl saved = controlRepo.save(policy);

        // 2) 디바이스 연결
        List<Device> devices = deviceRepo.findByRoom_RoomId(dto.getRoomId());
        if (devices.isEmpty()) {
            throw new ResourceNotFoundException(
                    "해당 방에 등록된 기기가 없습니다. roomId=" + dto.getRoomId());
        }
        devices.forEach(d -> d.setPolicy(saved));
        deviceRepo.saveAll(devices);
        saved.setDevices(devices);

        // 3) AI 전송 이벤트 발행 (payload: policyId)
        publisher.publishEvent(new AiPolicySendEvent(saved.getControlId()));

        return DeviceAutoControlMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DeviceAutoControlResponseDto> getAllAutoControls() {
        return controlRepo.findAll()
                .stream()
                .map(DeviceAutoControlMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeviceAutoControlResponseDto getAutoControlById(Long id) {
        DeviceAutoControl entity = controlRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("자동제어 정책이 없습니다. id=" + id));
        return DeviceAutoControlMapper.toResponseDto(entity);
    }

    @Transactional
    public DeviceAutoControlResponseDto updateAutoControl(Long id,
                                                          DeviceAutoControlRequestDto dto) {
        DeviceAutoControl existing = controlRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("자동제어 정책이 없습니다. id=" + id));

        DeviceAutoControlMapper.toEntity(dto, existing);
        DeviceAutoControl saved = controlRepo.save(existing);

        publisher.publishEvent(new AiPolicySendEvent(saved.getControlId()));
        return DeviceAutoControlMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteAutoControl(Long id) {
        // 정책 존재 확인
        DeviceAutoControl policy = controlRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("자동제어 정책이 없습니다. id=" + id));

        // 1) 디바이스와 연결 해제
        List<Device> devices = policy.getDevices();
        if (devices != null && !devices.isEmpty()) {
            devices.forEach(d -> d.setPolicy(null));
            deviceRepo.saveAll(devices);
        }

        // 2) 정책 삭제
        controlRepo.delete(policy);
    }
}
