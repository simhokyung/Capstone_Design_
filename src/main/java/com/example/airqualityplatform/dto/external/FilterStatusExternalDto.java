// src/main/java/com/example/airqualityplatform/dto/external/FilterStatusExternalDto.java
package com.example.airqualityplatform.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterStatusExternalDto {

    private Components components;

    @Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Components {
        private Main main;
    }

    @Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        /** 이 한 줄이 핵심입니다! */
        @JsonProperty("custom.hepaFilter")
        private HepaFilter hepaFilter;
    }

    @Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HepaFilter {
        private WithUnit    hepaFilterCapacity;
        private SimpleDouble hepaFilterUsage;
        private SimpleDouble hepaFilterUsageStep;
        private StatusWrap   hepaFilterStatus;
        private ListWrap     hepaFilterResetType;
        private DateWrap     hepaFilterLastResetDate;
    }

    @Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WithUnit {
        private Double value;
        private String unit;
        private String timestamp;
    }

    @Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SimpleDouble {
        private Double value;
        private String timestamp;
    }

    @Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusWrap {
        private String value;
        private String timestamp;
    }

    @Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListWrap {
        private List<String> value;
        private String timestamp;
    }

    @Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DateWrap {
        private Instant value;
        private String timestamp;
    }
}
