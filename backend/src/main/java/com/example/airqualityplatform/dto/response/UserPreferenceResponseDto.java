package com.example.airqualityplatform.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * 사용자 기준치 정보 응답 DTO.
 */
@Getter
@Setter
public class UserPreferenceResponseDto {
    private Long preferenceId;     // UserPreference 고유 ID
    private Long userId;           // 사용자 ID (1:1 관계)
    private Double pm25Threshold;
    private Double pm100Threshold;
    private Double co2Threshold;
    private Double vocThreshold;
    private Date createdAt;
    private Date updatedAt;
}
