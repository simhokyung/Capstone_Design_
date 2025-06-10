package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.SmartThingsToken;
import com.example.airqualityplatform.repository.SmartThingsTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SmartThingsTokenService {

    private final SmartThingsTokenRepository repo;

    public SmartThingsTokenService(SmartThingsTokenRepository repo) {
        this.repo = repo;
    }

    /** 외부에서 전송해 주는 새 토큰을 저장 */
    @Transactional
    public void saveToken(String bearerToken) {
        SmartThingsToken t = SmartThingsToken.builder()
                .token(bearerToken)
                .receivedAt(Instant.now())
                .build();
        repo.save(t);
    }

    /** 가장 최신 저장된 토큰을 반환 (없으면 예외) */
    public String getCurrentToken() {
        SmartThingsToken t = repo.findTopByOrderByReceivedAtDesc();
        if (t == null) {
            throw new IllegalStateException("No SmartThings token stored");
        }
        return t.getToken();
    }
}
