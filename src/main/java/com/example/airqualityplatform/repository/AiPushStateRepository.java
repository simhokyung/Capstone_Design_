package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.AiPushState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiPushStateRepository extends JpaRepository<AiPushState, Long> {
}