// src/main/java/com/example/airqualityplatform/service/AiPolicyListener.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.event.AiPolicySendEvent;
import com.example.airqualityplatform.dto.request.AiPolicyRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiPolicyListener {
    private final RestTemplate rt;

    @Async
    @EventListener  // AFTER_COMMIT 로직 필요 없으니 일반 이벤트 리스너로
    public void onPolicySend(AiPolicySendEvent evt) {
        AiPolicyRequestDto dto = evt.getPayload();
        try {
            rt.postForEntity(
                    "http://52.64.178.83:8000/receive/userstandard",
                    dto,
                    Void.class
            );
            log.info("AI로 정책 전송 완료 policyId={}", dto.getPolicyId());
        } catch (Exception ex) {
            log.error("AI 정책 전송 실패 policyId={} err={}", dto.getPolicyId(), ex.getMessage());
        }
    }
}
