package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    /** 외부식별자(deviceId)로 조회 */
    Optional<Device> findByDeviceId(String deviceId);
    List<Device> findByRoom_RoomId(Long roomId);

}
