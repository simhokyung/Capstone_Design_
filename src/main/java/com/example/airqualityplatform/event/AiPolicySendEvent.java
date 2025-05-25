// src/main/java/com/example/airqualityplatform/event/AiPolicySendEvent.java
package com.example.airqualityplatform.event;

import com.example.airqualityplatform.dto.request.AiPolicyRequestDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AiPolicySendEvent {
    private final AiPolicyRequestDto payload;
}
