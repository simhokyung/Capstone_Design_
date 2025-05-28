package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHome_HomeId(Long homeId);

    @Override
    @EntityGraph(attributePaths = "sensors")
    List<Room> findAll();
}
