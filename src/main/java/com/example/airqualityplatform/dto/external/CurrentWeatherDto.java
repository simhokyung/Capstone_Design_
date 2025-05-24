// src/main/java/com/example/airqualityplatform/dto/external/CurrentWeatherDto.java
package com.example.airqualityplatform.dto.external;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CurrentWeatherDto {
    private long dt;
    private Main main;

    @Getter @Setter
    public static class Main {
        private double temp;
    }
}
