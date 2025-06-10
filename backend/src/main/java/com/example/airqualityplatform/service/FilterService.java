// src/main/java/com/example/airqualityplatform/service/FilterService.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.Filter;
import com.example.airqualityplatform.domain.Sensor;
import com.example.airqualityplatform.dto.request.FilterRequestDto;
import com.example.airqualityplatform.dto.response.FilterResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.dto.mapper.FilterMapper;
import com.example.airqualityplatform.repository.DeviceRepository;
import com.example.airqualityplatform.repository.FilterRepository;
import com.example.airqualityplatform.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilterService {

    private final FilterRepository filterRepository;
    private final DeviceRepository deviceRepository;
    private final SensorRepository sensorRepository;

    @Transactional
    public FilterResponseDto createFilter(FilterRequestDto dto) {
        Device device = deviceRepository.findByDeviceId(dto.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("기기를 찾을 수 없습니다. id: " + dto.getDeviceId()));
        Sensor sensor = sensorRepository.findBySensorId(dto.getSensorId())
                .orElseThrow(() -> new ResourceNotFoundException("센서를 찾을 수 없습니다. id: " + dto.getSensorId()));
        Filter filter = FilterMapper.toEntity(dto, null);
        filter.setDevice(device);
        filter.setSensor(sensor);
        Filter saved = filterRepository.save(filter);
        return FilterMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<FilterResponseDto> getAllFilters() {
        return filterRepository.findAll().stream()
                .map(FilterMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FilterResponseDto getFilterById(Long id) {
        Filter filter = filterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("필터를 찾을 수 없습니다. id: " + id));
        return FilterMapper.toResponseDto(filter);
    }

    @Transactional
    public FilterResponseDto updateFilter(Long id, FilterRequestDto dto) {
        Filter existing = filterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("필터를 찾을 수 없습니다. id: " + id));
        Device device = deviceRepository.findByDeviceId(dto.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("기기를 찾을 수 없습니다. id: " + dto.getDeviceId()));
        Sensor sensor = sensorRepository.findBySensorId(dto.getSensorId())
                .orElseThrow(() -> new ResourceNotFoundException("센서를 찾을 수 없습니다. id: " + dto.getSensorId()));
        FilterMapper.toEntity(dto, existing);
        existing.setDevice(device);
        existing.setSensor(sensor);
        Filter saved = filterRepository.save(existing);
        return FilterMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteFilter(Long id) {
        if (!filterRepository.existsById(id)) {
            throw new ResourceNotFoundException("필터를 찾을 수 없습니다. id: " + id);
        }
        filterRepository.deleteById(id);
    }
}
