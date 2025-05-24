// src/main/java/com/example/airqualityplatform/dto/external/WeatherbitCityDto.java
package com.example.airqualityplatform.dto.external;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WeatherbitCityDto {
    private String city_name;
    private Double lat;
    private Double lon;
    // 필요시 country_code 등 추가
}
