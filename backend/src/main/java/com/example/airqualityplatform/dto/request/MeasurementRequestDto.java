// src/main/java/com/example/airqualityplatform/dto/request/MeasurementRequestDto.java
package com.example.airqualityplatform.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter
public class MeasurementRequestDto {

    /** 하드웨어의 device_id → Sensor.sensorId 로 매핑 */
    @JsonProperty("device_id")
    private Long sensorId;

    /** ISO-8601 timestamp (optional) */
    private Date timestamp;

    /** Unix 초 (timestamp 없으면 사용) */
    @JsonProperty("unix")
    private Long unix;

    /** 온도 */
    @JsonProperty("temp")
    private Double temperature;

    /** 습도 */
    @JsonProperty("hum")
    private Double humidity;

    /** CO₂ */
    @JsonProperty("co2")
    private Double co2;

    /** TVOC */
    @JsonProperty("tvoc")
    private Double voc;

    /** PM2.5 – baseline */
    @JsonProperty("pm25_b")
    private Double pm25_b;

    /** PM2.5 – measured */
    @JsonProperty("pm25_m")
    private Double pm25_m;

    /** PM2.5 – total */
    @JsonProperty("pm25_t")
    private Double pm25_t;

    /** PM10 – baseline */
    @JsonProperty("pm100_b")
    private Double pm100_b;

    /** PM10 – measured */
    @JsonProperty("pm100_m")
    private Double pm100_m;

    /** PM10 – total */
    @JsonProperty("pm100_t")
    private Double pm100_t;
}