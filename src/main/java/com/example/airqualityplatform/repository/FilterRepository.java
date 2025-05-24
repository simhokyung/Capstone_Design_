package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilterRepository extends JpaRepository<Filter, Long> {
    Optional<Filter> findByDevice_DeviceId(String deviceId);
}
