// src/main/java/com/example/airqualityplatform/event/AiPolicySendEvent.java
package com.example.airqualityplatform.event;

import com.example.airqualityplatform.dto.request.AiPolicyRequestDto;
import lombok.Getter;

/**
 * AI 서버로 정책을 보낼 때 트랜잭션 커밋 후 발행되는 이벤트.
 * payload 에는 DB와 무관한 순수 DTO만 담겨 있습니다.
 */
@Getter
public class AiPolicySendEvent {
    private final AiPolicyRequestDto payload;

    public AiPolicySendEvent(AiPolicyRequestDto payload) {
        this.payload = payload;
    }
}
