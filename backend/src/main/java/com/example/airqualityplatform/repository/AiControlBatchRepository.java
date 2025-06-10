package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.AiControlBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// AiControlBatchRepository.java
public interface AiControlBatchRepository extends JpaRepository<AiControlBatch, Long> {
    Optional<AiControlBatch> findFirstByOrderByTimestampDesc();
}
