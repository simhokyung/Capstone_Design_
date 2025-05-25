// src/main/java/com/example/airqualityplatform/service/DeviceAutoControlService.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.DeviceAutoControl;
import com.example.airqualityplatform.dto.mapper.DeviceAutoControlMapper;
import com.example.airqualityplatform.dto.request.DeviceAutoControlRequestDto;
import com.example.airqualityplatform.dto.request.AiPolicyRequestDto;
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
import java.util.stream.Collectors;

/**
 * 자동제어 정책 CRUD + AI 연동 이벤트 발행
 */
@Service
@RequiredArgsConstructor
public class DeviceAutoControlService {

    private final DeviceAutoControlRepository controlRepo;
    private final DeviceRepository deviceRepo;
    private final ApplicationEventPublisher publisher;

    /**
     * 1) 정책 저장
     * 2) Room 의 모든 디바이스에 정책 연결
     * 3) 커밋 직후 AI 전송 이벤트 발행
     */
    @Transactional
    public DeviceAutoControlResponseDto createAutoControl(DeviceAutoControlRequestDto dto) {

        /* 1) 정책 저장 */
        DeviceAutoControl policy = DeviceAutoControlMapper.toEntity(dto, null);
        DeviceAutoControl saved  = controlRepo.save(policy);

        /* 2) 디바이스 연결 */
        List<Device> devices = deviceRepo.findByRoom_RoomId(dto.getRoomId());
        if (devices.isEmpty()) {
            throw new ResourceNotFoundException(
                    "해당 방에 등록된 기기가 없습니다. roomId=" + dto.getRoomId());
        }
        devices.forEach(d -> d.setPolicy(saved));
        deviceRepo.saveAll(devices);
        saved.setDevices(devices);      // 영속 컨텍스트 내 컬렉션 세팅

        /* 3) AI 전송 DTO → 이벤트 발행 (AFTER_COMMIT 리스너가 비동기로 처리) */
        AiPolicyRequestDto aiDto = DeviceAutoControlMapper.toAiDto(saved);
        publisher.publishEvent(new AiPolicySendEvent(aiDto));

        return DeviceAutoControlMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DeviceAutoControlResponseDto> getAllAutoControls() {
        return controlRepo.findAll()
                .stream()
                .map(DeviceAutoControlMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeviceAutoControlResponseDto getAutoControlById(Long id) {
        DeviceAutoControl entity = controlRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("자동제어 정책이 없습니다. id=" + id));
        return DeviceAutoControlMapper.toResponseDto(entity);
    }

    /**
     * 정책 수정 후 AI 전송 이벤트 재발행
     */
    @Transactional
    public DeviceAutoControlResponseDto updateAutoControl(Long id,
                                                          DeviceAutoControlRequestDto dto) {

        DeviceAutoControl existing = controlRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("자동제어 정책이 없습니다. id=" + id));

        DeviceAutoControlMapper.toEntity(dto, existing);
        DeviceAutoControl saved = controlRepo.save(existing);

        publisher.publishEvent(
                new AiPolicySendEvent(DeviceAutoControlMapper.toAiDto(saved)));

        return DeviceAutoControlMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteAutoControl(Long id) {
        if (!controlRepo.existsById(id)) {
            throw new ResourceNotFoundException(
                    "자동제어 정책이 없습니다. id=" + id);
        }
        controlRepo.deleteById(id);
    }
}
