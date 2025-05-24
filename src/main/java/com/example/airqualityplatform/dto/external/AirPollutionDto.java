package com.example.airqualityplatform.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class AirPollutionDto {
    private List<Record> list;

    @Getter @Setter
    public static class Record {
        private long dt;
        private Main main;
        private Map<String, Double> components;
    }

    @Getter @Setter
    public static class Main {
        private int aqi;  // 1(좋음) ~ 5(매우나쁨)
    }
}