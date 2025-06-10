package com.example.airqualityplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter @Setter
public class DeviceStatusResponseDto {
    private Long id;
    private String deviceId;

    private Instant statusAt;
    private Long energy;
    private String powerState;
    private String fanMode;


    private Instant fetchedAt;
}
