package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.DeviceStatus;
import com.example.airqualityplatform.dto.ai.AiDeviceStatusDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceStatusRepository extends JpaRepository<DeviceStatus, Long> {
    List<DeviceStatus> findByDevice_DeviceIdOrderByStatusAtAsc(String deviceId);  // ✅ 작동함

    List<DeviceStatus> findByFetchedAtAfter(Instant since);
    // 최신 한 건
    Optional<DeviceStatus> findTopByDevice_IdOrderByStatusAtDesc(Long deviceId);

    List<DeviceStatus> findByDevice_IdAndStatusAtBetweenOrderByStatusAtAsc(
            Long deviceId, Instant from, Instant to);

    // 특정 시점 이전의 가장 가까운 DeviceStatus 조회
    Optional<DeviceStatus> findTopByDevice_IdAndStatusAtLessThanEqualOrderByStatusAtDesc(Long deviceId, Instant timestamp);
    @Query("""
      select new com.example.airqualityplatform.dto.ai.AiDeviceStatusDto(
        s.id,
        s.device.deviceId,
        s.statusAt,
        s.energy,
        s.powerState,
        s.fanMode,
        s.fetchedAt
      )
      from DeviceStatus s
      where s.fetchedAt > :since
      order by s.fetchedAt asc
    """)
    List<AiDeviceStatusDto> findNewStatuses(@Param("since") Instant since);

}
