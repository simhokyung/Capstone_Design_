package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.SmartThingsToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmartThingsTokenRepository extends JpaRepository<SmartThingsToken, Long> {
    // 가장 최근에 받은 토큰 한 건을 조회
    SmartThingsToken findTopByOrderByReceivedAtDesc();
}
