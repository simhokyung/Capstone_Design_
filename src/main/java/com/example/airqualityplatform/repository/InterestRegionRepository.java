package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.InterestRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRegionRepository extends JpaRepository<InterestRegion, Long> {
    /** 특정 사용자의 모든 관심 지역 조회 */
    List<InterestRegion> findByUser_UserId(Long userId);
}
