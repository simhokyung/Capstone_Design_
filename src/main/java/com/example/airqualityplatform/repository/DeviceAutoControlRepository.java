package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.DeviceAutoControl;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeviceAutoControlRepository extends JpaRepository<DeviceAutoControl, Long> {

    @Query("SELECT c FROM DeviceAutoControl c " +
            "JOIN FETCH c.devices d " +
            "JOIN FETCH d.room " +
            "WHERE c.controlId = :id")
    Optional<DeviceAutoControl> findWithDevicesAndRoomById(@Param("id") Long id);
}