// src/main/java/com/example/airqualityplatform/dto/request/AiPolicyRequestDto.java
package com.example.airqualityplatform.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiPolicyRequestDto {
    private Long policyId;
    private Long roomId;
    private Thresholds thresholds;
    private List<String> deviceIds;

    @Getter
    @Setter
    public static class Thresholds {
        private Double pm25;
        private Double pm100;
        private Double co2;
        private Double voc;
    }
}
