package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.domain.Schedule;
import com.example.airqualityplatform.dto.request.ScheduleRequestDto;
import com.example.airqualityplatform.dto.response.ScheduleResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.dto.mapper.ScheduleMapper;
import com.example.airqualityplatform.repository.DeviceRepository;
import com.example.airqualityplatform.repository.ScheduleRepository;
import com.example.airqualityplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleResponseDto createSchedule(ScheduleRequestDto dto) {
        Device device = deviceRepository.findByDeviceId(dto.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "장치를 찾을 수 없습니다. deviceId: " + dto.getDeviceId()));
        User user = null;
        if (dto.getUserId() != null) {
            user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "사용자를 찾을 수 없습니다. userId: " + dto.getUserId()));
        }
        Schedule schedule = ScheduleMapper.toEntity(dto, null);
        schedule.setDevice(device);
        schedule.setUser(user);
        Schedule saved = scheduleRepository.save(schedule);
        return ScheduleMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(ScheduleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "일정을 찾을 수 없습니다. id: " + id));
        return ScheduleMapper.toResponseDto(schedule);    }

    @Transactional
    public ScheduleResponseDto updateSchedule(Long id, ScheduleRequestDto dto) {
        Schedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "일정을 찾을 수 없습니다. id: " + id));
        Device device = deviceRepository.findByDeviceId(dto.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "장치를 찾을 수 없습니다. deviceId: " + dto.getDeviceId()));
        existing.setDevice(device);
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "사용자를 찾을 수 없습니다. userId: " + dto.getUserId()));
            existing.setUser(user);
        }
        Schedule updated = ScheduleMapper.toEntity(dto, existing);
        Schedule saved = scheduleRepository.save(updated);
        return ScheduleMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("일정을 찾을 수 없습니다. id: " + id);
        }
        scheduleRepository.deleteById(id);
    }
}