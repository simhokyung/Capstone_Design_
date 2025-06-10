package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // 추가 검색 메서드가 필요하면 이곳에 선언 (예: deviceId로 검색)
}