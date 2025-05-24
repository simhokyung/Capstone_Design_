package com.example.airqualityplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class PurifierRecommendation {
    private Long sensorId;        // 추천 센서 ID
    private Double xCoordinate;   // 추천 좌표 X
    private Double yCoordinate;   // 추천 좌표 Y
    private Double maxPm25;       // 최대 PM2.5 값
    private Double averagePm25;   // 평균 PM2.5 값
    private String message;       // 추천 메시지
    private Date timestamp;       // 해당 측정 시각
}
