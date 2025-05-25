// src/main/java/com/example/airqualityplatform/service/AiPolicyListener.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.event.AiPolicySendEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiPolicyListener {

    private final RestTemplate rt;
    private static final String AI_URL = "http://52.64.178.83:8000/receive/userstandard";

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPolicySend(AiPolicySendEvent evt) {

        try {
            rt.postForEntity(AI_URL, evt.getPayload(), Void.class);
            log.info("AI 정책 전송 성공 –– policyId={}", evt.getPayload().getPolicyId());
        } catch (Exception ex) {
            log.error("AI 정책 전송 실패 –– policyId={} err={}",
                    evt.getPayload().getPolicyId(), ex.getMessage(), ex);
        }
    }
}
