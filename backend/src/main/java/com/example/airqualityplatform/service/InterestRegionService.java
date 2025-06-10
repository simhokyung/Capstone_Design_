package com.example.airqualityplatform.service;

import com.example.airqualityplatform.client.OpenWeatherMapClient;
import com.example.airqualityplatform.domain.InterestRegion;
import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.external.GeoCodingDto;
import com.example.airqualityplatform.dto.request.InterestRegionRequestDto;
import com.example.airqualityplatform.dto.response.InterestRegionResponseDto;
import com.example.airqualityplatform.dto.mapper.InterestRegionMapper;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.InterestRegionRepository;
import com.example.airqualityplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterestRegionService {

    private final InterestRegionRepository regionRepo;
    private final UserRepository           userRepo;
    private final OpenWeatherMapClient     owmClient;

    @Transactional
    public InterestRegionResponseDto create(Long userId, InterestRegionRequestDto dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. id: " + userId));

        // 1) 지오코딩 호출
        GeoCodingDto geo = owmClient.geocode(dto.getRegionName().trim());

        // 2) 엔티티에 위경도 정보 포함
        InterestRegion region = InterestRegion.builder()
                .user(user)
                .regionName(dto.getRegionName().trim())
                .latitude(geo.getLat())
                .longitude(geo.getLon())
                .build();

        InterestRegion saved = regionRepo.save(region);
        return InterestRegionMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<InterestRegionResponseDto> findByUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다. id: " + userId);
        }
        return regionRepo.findByUser_UserId(userId).stream()
                .map(InterestRegionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long userId, Long regionId) {
        InterestRegion region = regionRepo.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("관심 지역을 찾을 수 없습니다. id: " + regionId));

        if (!region.getUser().getUserId().equals(userId)) {
            throw new ResourceNotFoundException("사용자의 관심 지역이 아닙니다. userId: " + userId);
        }
        regionRepo.delete(region);
    }
}