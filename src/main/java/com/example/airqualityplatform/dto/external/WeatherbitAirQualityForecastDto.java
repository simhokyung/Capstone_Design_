// src/main/java/com/example/airqualityplatform/dto/external/WeatherbitAirQualityForecastDto.java
package com.example.airqualityplatform.dto.external;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class WeatherbitAirQualityForecastDto {
    private List<AQData> data;

    @Getter @Setter
    public static class AQData {
        /** ISO timestamp, e.g. "2025-05-23T16:00:00" */
        @JsonProperty("timestamp_utc")
        private String timestampUtc;

        /** 실제로는 "pm25" 키로 오지만, 혹시 예전 버전의 "pm2_5" 키도 커버하도록 */
        @JsonAlias({"pm25", "pm2_5"})
        private Double pm25;

        private Double pm10;
        private Double o3;
        private Double no2;
        // 필요시 so2, co 등도 추가 가능
    }
}
