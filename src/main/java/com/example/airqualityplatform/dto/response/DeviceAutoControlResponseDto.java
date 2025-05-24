package com.example.airqualityplatform.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DeviceAutoControlResponseDto {
    private Long controlId;
    private Long roomId;
    private List<Long> deviceIds;
    private Double pm25Threshold;
    private Double co2Threshold;
    private Double pm100Threshold;
    private Double vocThreshold;
    private Boolean actionPower;
    private String actionMode;
    private Date createdAt;
    private Date updatedAt;
}
