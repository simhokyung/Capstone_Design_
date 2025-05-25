// src/main/java/com/example/airqualityplatform/repository/AiPredictionBatchRepository.java
package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.AiPredictionBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiPredictionBatchRepository extends JpaRepository<AiPredictionBatch, Long> {
    AiPredictionBatch findTopByOrderByTimestampDesc();
}
