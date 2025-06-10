// src/main/java/com/example/airqualityplatform/client/WeatherbitClient.java
package com.example.airqualityplatform.client;

import com.example.airqualityplatform.dto.external.WeatherbitAirQualityForecastDto;
import com.example.airqualityplatform.dto.external.WeatherbitWeatherForecastDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WeatherbitClient {

    private final WebClient weatherbitWebClient;

    @Value("${weatherbit.api-key}")
    private String apiKey;

    private <T> List<T> limitHours(List<T> list, int hours) {
        return list.stream().limit(hours).toList();
    }

    /** ▶ 수정된 부분: path 를 "/forecast/airquality" 로 변경 */
    public WeatherbitAirQualityForecastDto getHourlyAirQuality(double lat, double lon, int hours) {
        WeatherbitAirQualityForecastDto resp = weatherbitWebClient.get()
                .uri(uri -> uri
                        .path("/forecast/airquality")       // ← 여기
                        .queryParam("lat",      lat)
                        .queryParam("lon",      lon)
                        .queryParam("key",      apiKey)
                        .queryParam("hours",    hours)      // 선택: 원하는 시간 수 지정
                        .build())
                .retrieve()
                .bodyToMono(WeatherbitAirQualityForecastDto.class)
                .block();

        if (resp != null && resp.getData() != null) {
            resp.setData(limitHours(resp.getData(), hours));
        }
        return resp;
    }

    /** (날씨 예측) 역시 hours 파라미터 추가 가능 */
    public WeatherbitWeatherForecastDto getHourlyWeather(double lat, double lon, int hours) {
        WeatherbitWeatherForecastDto resp = weatherbitWebClient.get()
                .uri(uri -> uri
                        .path("/forecast/hourly")
                        .queryParam("lat",   lat)
                        .queryParam("lon",   lon)
                        .queryParam("key",   apiKey)
                        .queryParam("hours", hours)
                        .build())
                .retrieve()
                .bodyToMono(WeatherbitWeatherForecastDto.class)
                .block();

        if (resp != null && resp.getData() != null) {
            resp.setData(limitHours(resp.getData(), hours));
        }
        return resp;
    }
}