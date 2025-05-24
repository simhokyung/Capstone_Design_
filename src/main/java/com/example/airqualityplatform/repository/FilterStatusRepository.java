package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.FilterStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilterStatusRepository extends JpaRepository<FilterStatus, Long> {
    List<FilterStatus> findByFilter_FilterId(Long filterId);
    Optional<FilterStatus> findTopByFilter_FilterIdOrderByStatusAtDesc(Long filterId);
    // 누적 계산을 위해 오름차순 전체 이력 조회
    List<FilterStatus> findByFilter_FilterIdOrderByStatusAtAsc(Long filterId);
    List<FilterStatus>   findByFilter_FilterIdOrderByStatusAtDesc(Long filterId);
}
