// src/main/java/com/example/airqualityplatform/domain/FilterStatusScheduler.java
package com.example.airqualityplatform.domain;

import com.example.airqualityplatform.repository.FilterRepository;
import com.example.airqualityplatform.service.FilterStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilterStatusScheduler {

    private final FilterRepository filterRepository;
    private final FilterStatusService statusService;

    /**
     * 애플리케이션 시작 후 1분마다,
     * 등록된 모든 필터의 상태를 SmartThings에서 조회해 저장합니다.
     */
    @Scheduled(fixedDelay = 60 * 1000)
    public void syncAllFilterStatuses() {
        filterRepository.findAll().forEach(filter -> {
            try {
                statusService.fetchAndSaveStatus(filter.getFilterId());
            } catch (Exception e) {
                log.error("Failed to sync status for filter {}", filter.getFilterId(), e);
            }
        });
    }
}
