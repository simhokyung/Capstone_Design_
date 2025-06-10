// src/main/java/com/example/airqualityplatform/dto/external/WeatherbitWeatherForecastDto.java
package com.example.airqualityplatform.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class WeatherbitWeatherForecastDto {
    private List<WeatherData> data;

    @Getter @Setter
    public static class WeatherData {
        /** ISO timestamp, e.g. "2025-05-23T14:00:00" */
        @JsonProperty("timestamp_utc")
        private String timestampUtc;
        private Double temp;
    }
}
