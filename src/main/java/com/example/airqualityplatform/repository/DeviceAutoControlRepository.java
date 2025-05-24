package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.DeviceAutoControl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceAutoControlRepository extends JpaRepository<DeviceAutoControl, Long> {
    // 필요 시 커스텀 검색 메서드 선언 가능
}
