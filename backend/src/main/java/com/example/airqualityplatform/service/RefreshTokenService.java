package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.RefreshToken;
import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.exception.InvalidCredentialsException;
import com.example.airqualityplatform.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    @Value("${jwt.refresh.expiration-ms}")
    private long refreshTokenDurationMs;

    public RefreshToken create(User user) {
        repo.deleteByUser(user);  // 기존 토큰은 한 번에 하나만
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();
        return repo.save(token);
    }

    public void validate(String tokenStr) {
        RefreshToken token = repo.findByToken(tokenStr)
                .orElseThrow(() -> new InvalidCredentialsException("유효하지 않은 리프레시 토큰"));
        if (token.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(token);
            throw new InvalidCredentialsException("리프레시 토큰 만료됨");
        }
    }

    public void deleteByToken(String tokenStr) {
        RefreshToken token = repo.findByToken(tokenStr)
                .orElseThrow(() -> new InvalidCredentialsException("유효하지 않은 리프레시 토큰"));
        repo.delete(token);
    }

    public void deleteForUser(User user) {
        repo.deleteByUser(user);
    }
}
