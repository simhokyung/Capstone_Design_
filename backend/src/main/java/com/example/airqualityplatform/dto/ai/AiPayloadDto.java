package com.example.airqualityplatform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AiPayloadDto {
    private List<AiMeasurementDto>     measurements;
    private List<AiDeviceStatusDto>    statuses;
}
