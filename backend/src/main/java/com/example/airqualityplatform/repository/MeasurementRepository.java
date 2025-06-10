// src/main/java/com/example/airqualityplatform/repository/MeasurementRepository.java
package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.Measurement;
import com.example.airqualityplatform.dto.ai.AiMeasurementDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    List<Measurement> findBySensor_SensorIdOrderByTimestampAsc(Long sensorId);

    List<Measurement> findBySensor_SensorIdAndTimestampBetween(
            Long sensorId, Date start, Date end);

    Optional<Measurement> findTopBySensor_SensorIdOrderByTimestampDesc(Long sensorId);

    List<Measurement> findBySensor_Room_RoomIdAndTimestampBetweenOrderByTimestampAsc(
            Long roomId, Date start, Date end);

    /**
     * ★ Instant → Date 로 되돌려야 JPA가 제대로 구현합니다.
     */
    Optional<Measurement> findTopBySensor_SensorIdAndTimestampLessThanEqualOrderByTimestampDesc(
            Long sensorId,
            Date timestamp);

    List<Measurement> findBySensor_SensorIdAndTimestampBetweenOrderByTimestampAsc(
            Long sensorId, Date from, Date to);


    List<Measurement> findByTimestampAfter(Date since);

    @Query("""
    select new com.example.airqualityplatform.dto.ai.AiMeasurementDto(
      m.measurementId,
      m.sensor.sensorId,
      m.timestamp,
      m.temperature,
      m.humidity,
      m.co2,
      m.voc,
      m.pm25_b,
      m.pm25_m,
      m.pm25_t,
      m.pm100_b,
      m.pm100_m,
      m.pm100_t
    )
    from Measurement m
    where m.timestamp > :since
    order by m.timestamp asc
    """)
    List<AiMeasurementDto> findNewMeasurements(@Param("since") Date since);
}
