// src/main/java/com/example/airqualityplatform/service/AiPolicyListener.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.event.AiPolicySendEvent;
import com.example.airqualityplatform.dto.request.AiPolicyRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiPolicyListener {

    private final RestTemplate rt;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPolicySend(AiPolicySendEvent evt) {
        AiPolicyRequestDto payload = evt.getPayload();
        try {
            rt.postForEntity(
                    "http://52.64.178.83:8000/receive/userstandard",
                    payload,
                    Void.class
            );
            log.info("AI로 정책 전송 완료 policyId={}", payload.getPolicyId());
        } catch (Exception ex) {
            log.error("AI 정책 전송 실패 policyId={} err={}",
                    payload.getPolicyId(), ex.getMessage());
        }
    }
}
