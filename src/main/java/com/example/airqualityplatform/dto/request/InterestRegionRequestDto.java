package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InterestRegionRequestDto {

    @NotBlank(message = "regionName을 입력하세요.")
    @Size(max = 50, message = "regionName은 최대 50자까지 가능합니다.")
    private String regionName;
}