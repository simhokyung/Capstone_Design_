// src/main/java/com/example/airqualityplatform/service/ForecastService.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.client.WeatherbitClient;
import com.example.airqualityplatform.dto.external.WeatherbitAirQualityForecastDto;
import com.example.airqualityplatform.dto.external.WeatherbitWeatherForecastDto;
import com.example.airqualityplatform.dto.response.HourlyForecastDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final WeatherbitClient weatherbit;
    private static final int HOURS = 24;

    public List<HourlyForecastDto> getForecast(double lat, double lon) {
        // 1) 시간별 기온
        WeatherbitWeatherForecastDto wxDto = weatherbit.getHourlyWeather(lat, lon, HOURS);
        // 2) 시간별 공기질
        WeatherbitAirQualityForecastDto aqDto = weatherbit.getHourlyAirQuality(lat, lon, HOURS);

        // 3) 기온 데이터 인덱싱 (timestampUtc → WeatherData)
        Map<String, WeatherbitWeatherForecastDto.WeatherData> wxMap =
                wxDto.getData().stream()
                        .collect(Collectors.toMap(
                                WeatherbitWeatherForecastDto.WeatherData::getTimestampUtc,
                                Function.identity()
                        ));

        // 4) AQ 데이터와 기온 매핑하여 DTO 생성
        return aqDto.getData().stream()
                .map(aq -> {
                    String ts = aq.getTimestampUtc();
                    Double temp = wxMap.containsKey(ts)
                            ? wxMap.get(ts).getTemp()
                            : null;

                    return HourlyForecastDto.builder()
                            .datetime(ts)
                            .temperature(temp)
                            .pm25(aq.getPm25())
                            .pm10(aq.getPm10())
                            .o3(aq.getO3())
                            .no2(aq.getNo2())
                            .build();
                })
                .toList();
    }
}
