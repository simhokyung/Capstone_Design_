// src/main/java/com/example/airqualityplatform/dto/response/FilterLifeResponseDto.java
package com.example.airqualityplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FilterLifeResponseDto {


    /** 남은 필터 수명 비율(%) */
    private final double remainingPercent;
}
