package com.example.airqualityplatform.dto.external;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GeoCodingDto {
    private String name;
    private Double lat;
    private Double lon;
    private String country;
}