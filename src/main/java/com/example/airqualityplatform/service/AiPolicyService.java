// src/main/java/com/example/airqualityplatform/service/AiPolicyService.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.DeviceAutoControl;
import com.example.airqualityplatform.dto.mapper.DeviceAutoControlMapper;
import com.example.airqualityplatform.dto.request.AiPolicyRequestDto;
import com.example.airqualityplatform.repository.DeviceAutoControlRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiPolicyService {
    private static final Logger log = LoggerFactory.getLogger(AiPolicyService.class);
    private final DeviceAutoControlRepository repo;
    private final RestTemplate rt;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendPolicyToAiAsync(Long controlId) {
        DeviceAutoControl policy = repo.findById(controlId)
                .orElseThrow(() -> new IllegalStateException("정책이 없습니다. id=" + controlId));

        AiPolicyRequestDto payload = DeviceAutoControlMapper.toAiDto(policy);

        try {
            rt.postForEntity(
                    "http://52.64.178.83:8000/receive/userstandard",
                    payload,
                    Void.class
            );
            log.info("AI로 정책 전송 완료 id={}", controlId);
        } catch (Exception e) {
            log.error("AI 정책 전송 실패 id={} err={}", controlId, e.getMessage());
        }
    }
}
