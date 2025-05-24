// src/main/java/com/example/airqualityplatform/repository/RefreshTokenRepository.java
package com.example.airqualityplatform.repository;

import com.example.airqualityplatform.domain.RefreshToken;
import com.example.airqualityplatform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
