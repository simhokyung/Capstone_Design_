// src/main/java/com/example/airqualityplatform/client/OpenWeatherMapClient.java
package com.example.airqualityplatform.client;

import com.example.airqualityplatform.dto.external.AirPollutionDto;
import com.example.airqualityplatform.dto.external.CurrentWeatherDto;
import com.example.airqualityplatform.dto.external.GeoCodingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class OpenWeatherMapClient {

    private final WebClient airQualityWebClient;
    private final WebClient geocodeWebClient;
    private final WebClient currentWeatherWebClient;

    @Value("${air.quality.api-key}")
    private String apiKey;

    public GeoCodingDto geocode(String regionName) {
        GeoCodingDto[] resp = geocodeWebClient.get()
                .uri(uri -> uri
                        .queryParam("q", regionName)
                        .queryParam("limit", 1)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(GeoCodingDto[].class)
                .block();

        if (resp == null || resp.length == 0) {
            throw new IllegalArgumentException("지오코딩 결과가 없습니다: " + regionName);
        }
        return resp[0];
    }

    public AirPollutionDto getAirPollution(double lat, double lon) {
        return airQualityWebClient.get()
                .uri(uri -> uri
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(AirPollutionDto.class)
                .block();
    }

    public CurrentWeatherDto getCurrentWeather(double lat, double lon) {
        return currentWeatherWebClient.get()
                .uri(uri -> uri
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .bodyToMono(CurrentWeatherDto.class)
                .block();
    }
}
