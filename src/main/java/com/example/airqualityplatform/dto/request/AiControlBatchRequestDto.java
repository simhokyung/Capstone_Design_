package com.example.airqualityplatform.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AiControlBatchRequestDto {

    @NotBlank
    private String timestamp;

    @NotBlank
    private String deviceId;

    @NotEmpty
    @JsonProperty("control_result")  // ✅ 이게 없으면 무조건 null 됨
    private List<AiControlSegmentDto> control_result;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class AiControlSegmentDto {

        @NotNull @Min(0) @Max(59)
        private Integer startMinute;

        @NotNull @Min(1) @Max(60)
        private Integer endMinute;

        @NotBlank
        private String airPurifier;

        @NotBlank
        private String fanMode;

        @NotNull
        private Boolean ventilation;
    }
}
