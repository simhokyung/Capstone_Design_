package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    /** 방 ID 로 센서들 조회 */
    List<Sensor> findByRoom_RoomId(Long roomId);

    /** 외부 식별자(sensorId) 로 센서 단건 조회 */
    Optional<Sensor> findBySensorId(Long sensorId);
}
