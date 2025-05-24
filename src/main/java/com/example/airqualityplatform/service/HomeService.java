package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Home;
import com.example.airqualityplatform.dto.request.HomeRequestDto;
import com.example.airqualityplatform.dto.response.HomeResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.dto.mapper.HomeMapper;
import com.example.airqualityplatform.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeRepository homeRepository;

    @Transactional
    public HomeResponseDto createHome(HomeRequestDto dto) {
        Home home = HomeMapper.toEntity(dto, null);
        Home saved = homeRepository.save(home);
        return HomeMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<HomeResponseDto> getAllHomes() {
        return homeRepository.findAll().stream()
                .map(HomeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HomeResponseDto getHomeById(Long homeId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "집을 찾을 수 없습니다. id: " + homeId));
        return HomeMapper.toResponseDto(home);
    }

    @Transactional
    public HomeResponseDto updateHome(Long homeId, HomeRequestDto dto) {
        Home existing = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "집을 찾을 수 없습니다. id: " + homeId));
        HomeMapper.toEntity(dto, existing);
        Home saved = homeRepository.save(existing);
        return HomeMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteHome(Long homeId) {
        if (!homeRepository.existsById(homeId)) {
            throw new ResourceNotFoundException("집을 찾을 수 없습니다. id: " + homeId);
        }
        homeRepository.deleteById(homeId);
    }
}