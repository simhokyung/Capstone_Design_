package com.example.airqualityplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
//필요시 쓰기
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HeatmapDataResponse {
    private Long sensorId;
    private Double xCoordinate;
    private Double yCoordinate;
    private Double pm25;
    private Date timestamp;
}
