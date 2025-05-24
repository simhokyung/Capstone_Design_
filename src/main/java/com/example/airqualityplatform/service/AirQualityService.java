// src/main/java/com/example/airqualityplatform/service/AirQualityService.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.client.OpenWeatherMapClient;
import com.example.airqualityplatform.client.WeatherbitClient;
import com.example.airqualityplatform.domain.InterestRegion;
import com.example.airqualityplatform.dto.external.AirPollutionDto;
import com.example.airqualityplatform.dto.external.CurrentWeatherDto;
import com.example.airqualityplatform.dto.external.WeatherbitAirQualityForecastDto;
import com.example.airqualityplatform.dto.external.WeatherbitWeatherForecastDto;
import com.example.airqualityplatform.dto.response.AirQualityResponseDto;
import com.example.airqualityplatform.dto.response.HourlyForecastDto;
import com.example.airqualityplatform.dto.response.RegionForecastResponseDto;
import com.example.airqualityplatform.repository.InterestRegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirQualityService {

    private final InterestRegionRepository regionRepo;
    private final OpenWeatherMapClient owmClient;
    private final WeatherbitClient    weatherbitClient;
    private static final int HOURS = 24;

    public List<AirQualityResponseDto> getAirQualityForUser(Long userId) {
        var regions = regionRepo.findByUser_UserId(userId);

        return regions.stream().map(r -> {
            var pol     = owmClient.getAirPollution(r.getLatitude(), r.getLongitude());
            var record  = pol.getList().get(0);
            var comp    = record.getComponents();
            var weather = owmClient.getCurrentWeather(r.getLatitude(), r.getLongitude());

            return AirQualityResponseDto.builder()
                    .regionName(r.getRegionName())
                    .latitude(r.getLatitude())
                    .longitude(r.getLongitude())
                    .aqi(record.getMain().getAqi())
                    .pm25(comp.get("pm2_5"))
                    .pm10(comp.get("pm10"))
                    .o3(comp.get("o3"))
                    .no2(comp.get("no2"))
                    .temperature(weather.getMain().getTemp())
                    .fetchedAt(Instant.ofEpochSecond(record.getDt()))
                    .build();
        }).toList();
    }

    public List<RegionForecastResponseDto> getForecastForUser(Long userId) {
        var regions = regionRepo.findByUser_UserId(userId);

        return regions.stream().map(r -> {
            double lat = r.getLatitude();
            double lon = r.getLongitude();

            // 1) 24시간 치 공기질 예측
            WeatherbitAirQualityForecastDto aqDto = weatherbitClient.getHourlyAirQuality(lat, lon, HOURS);
            // 2) 24시간 치 기온 예측
            WeatherbitWeatherForecastDto   wxDto = weatherbitClient.getHourlyWeather(lat, lon, HOURS);

            // 기온 데이터: timestampUtc → WeatherData
            Map<String, WeatherbitWeatherForecastDto.WeatherData> wxMap =
                    wxDto.getData().stream()
                            .collect(Collectors.toMap(
                                    WeatherbitWeatherForecastDto.WeatherData::getTimestampUtc,
                                    Function.identity()
                            ));

            // AQ 데이터와 기온 매핑
            List<HourlyForecastDto> hourly = aqDto.getData().stream()
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
                    .collect(Collectors.toList());

            return RegionForecastResponseDto.builder()
                    .regionId(r.getId())
                    .regionName(r.getRegionName())
                    .latitude(lat)
                    .longitude(lon)
                    .forecast(hourly)
                    .build();
        }).collect(Collectors.toList());
    }
}
