package com.example.airqualityplatform.service;

import com.example.airqualityplatform.dto.response.FilterLifeResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.FilterRepository;
import com.example.airqualityplatform.repository.FilterStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FilterLifeService {

    private final FilterRepository filterRepository;
    private final FilterStatusRepository statusRepository;

    @Transactional(readOnly = true)
    public FilterLifeResponseDto calculateFilterLife(Long filterId) {
        // 필터 존재 확인
        filterRepository.findById(filterId)
                .orElseThrow(() -> new ResourceNotFoundException("필터를 찾을 수 없습니다. id=" + filterId));

        // 최신 상태 1건 조회
        var status = statusRepository
                .findTopByFilter_FilterIdOrderByStatusAtDesc(filterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "필터 상태가 없습니다. filterId=" + filterId));

        // 남은 수명 및 백분율 계산
        double remaining = Math.max(0.0,
                status.getCapacity() - status.getEffectiveUsageAccumulated());
        double percent = status.getCapacity() > 0
                ? (remaining / status.getCapacity()) * 100.0
                : 0.0;

        // 필요한 필드만 반환: remaining 시간과 percent (%)
        return new FilterLifeResponseDto(percent);
    }
}
