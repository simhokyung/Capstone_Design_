// src/main/java/com/example/airqualityplatform/event/AiPolicySendEvent.java
package com.example.airqualityplatform.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AiPolicySendEvent extends ApplicationEvent {
    private final Long controlId;
    public AiPolicySendEvent(Object source, Long controlId) {
        super(source);
        this.controlId = controlId;
    }
}
