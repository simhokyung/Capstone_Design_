// src/main/java/com/example/airqualityplatform/dto/response/MeasurementResponseDto.java
package com.example.airqualityplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class MeasurementResponseDto {

    private Long measurementId;
    private Long sensorId;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            timezone = "UTC")
    private Date timestamp;

    private Double temperature;
    private Double humidity;
    private Double co2;
    private Double voc;
    private Double pm25_b;
    private Double pm25_m;
    private Double pm25_t;
    private Double pm100_b;
    private Double pm100_m;
    private Double pm100_t;
}
