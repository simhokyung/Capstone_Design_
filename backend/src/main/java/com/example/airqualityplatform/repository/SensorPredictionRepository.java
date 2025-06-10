// src/main/java/com/example/airqualityplatform/repository/SensorPredictionRepository.java
package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.SensorPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorPredictionRepository extends JpaRepository<SensorPrediction, Long> {
    List<SensorPrediction> findByBatch_BatchId(Long batchId);
}
