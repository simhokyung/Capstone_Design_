package com.example.airqualityplatform.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceStatusExternalDto {

    /** SmartThings가 반환하는 components 블록 전체 */
    @JsonProperty("components")
    private Components components;

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Components {
        /** 주로 main 컴포넌트만 쓰면 됩니다 */
        @JsonProperty("main")
        private Main main;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        @JsonProperty("powerConsumptionReport")
        private PowerConsumptionReport powerConsumptionReport;

        /** "switch" 필드를 받기 위해 @JsonProperty 필요 */
        @JsonProperty("switch")
        private SwitchDto _switch;

        @JsonProperty("airConditionerFanMode")
        private AirConditionerFanMode airConditionerFanMode;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PowerConsumptionReport {
        @JsonProperty("powerConsumption")
        private PowerConsumption powerConsumption;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PowerConsumption {
        private ValueWrapper value;
        private String timestamp;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ValueWrapper {
        private Long energy;
        private String start;
        private String end;
        // 그 외 필드는 무시
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SwitchDto {
        @JsonProperty("switch")
        private InnerString _switch;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InnerString {
        private String value;
        private String timestamp;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AirConditionerFanMode {
        private InnerString fanMode;
        // supportedAcFanModes, availableAcFanModes 등은 무시
    }
}
