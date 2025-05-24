package com.example.airqualityplatform.dto.response;

import lombok.Getter;
import lombok.Setter;
//필요시 쓰기
@Getter
@Setter
public class PredictionResponse {
    private Double predictedValue; // 예: 예측된 PM2.5 값 등
    private String message;
}
