package com.example.airqualityplatform.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter @Setter
public class DeviceResponseDto {
    private Long id;
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private Double xCoordinate;
    private Double yCoordinate;
    private Boolean power;
    private String fanMode;
    private Date registeredAt;
    private Date updatedAt;
    private Long roomId;
    // Optional policyId
    private Long policyId;
}