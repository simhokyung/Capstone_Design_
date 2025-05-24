package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {
    // 기본 CRUD 메서드 자동 제공
}
