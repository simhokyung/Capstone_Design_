package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AiControlBatchRequestDto {
    @NotBlank(message = "timestamp을 입력하세요.")
    private String timestamp;  // ISO8601, ex. "2025-05-21T14:35:25Z"

    @NotBlank(message = "deviceId를 입력하세요.")
    private String deviceId;

    @NotEmpty(message = "control_result는 최소 1개 이상이어야 합니다.")
    private List<AiControlSegmentDto> control_result;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class AiControlSegmentDto {
        @NotNull(message = "startMinute을 입력하세요.")
        @Min(0) @Max(59)
        private Integer startMinute;

        @NotNull(message = "endMinute을 입력하세요.")
        @Min(1) @Max(60)
        private Integer endMinute;

        @NotBlank(message = "airPurifier을 입력하세요.")
        private String airPurifier;

        @NotBlank(message = "fanMode를 입력하세요.")
        private String fanMode;

        @NotNull(message = "ventilation을 입력하세요.")
        private Boolean ventilation;
    }
}