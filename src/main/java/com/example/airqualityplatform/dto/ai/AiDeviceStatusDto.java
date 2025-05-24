package com.example.airqualityplatform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiDeviceStatusDto {
    private Long    id;
    private String  deviceId;
    private Instant statusAt;
    private Long    energy;
    private String  powerState;
    private String  fanMode;
    private Instant fetchedAt;
}
