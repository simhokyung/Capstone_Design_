// src/main/java/com/example/airqualityplatform/event/AiPolicySendEvent.java
package com.example.airqualityplatform.event;

import lombok.Getter;

/**
 * AI 서버로 정책을 보낼 때 트랜잭션 커밋 후 발행되는 이벤트.
 * payload 에는 policyId만 담깁니다.
 */
@Getter
public class AiPolicySendEvent {
    private final Long policyId;

    public AiPolicySendEvent(Long policyId) {
        this.policyId = policyId;
    }
}