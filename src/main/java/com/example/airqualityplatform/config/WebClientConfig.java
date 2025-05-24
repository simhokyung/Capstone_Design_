// src/main/java/com/example/airqualityplatform/config/WebClientConfig.java
package com.example.airqualityplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${air.quality.base-url}")
    private String airQualityBaseUrl;

    @Value("${air.quality.geocode-url}")
    private String geocodeBaseUrl;

    @Value("${air.weather.base-url}")
    private String weatherBaseUrl;

    @Value("${weatherbit.base-url}")
    private String weatherbitBaseUrl;

    @Bean
    public WebClient airQualityWebClient() {
        return WebClient.builder()
                .baseUrl(airQualityBaseUrl)
                .build();
    }

    @Bean
    public WebClient geocodeWebClient() {
        return WebClient.builder()
                .baseUrl(geocodeBaseUrl)
                .build();
    }

    @Bean
    public WebClient currentWeatherWebClient() {
        return WebClient.builder()
                .baseUrl(weatherBaseUrl)
                .build();
    }

    @Bean
    public WebClient weatherbitWebClient() {
        return WebClient.builder()
                .baseUrl(weatherbitBaseUrl)
                .build();
    }
}
