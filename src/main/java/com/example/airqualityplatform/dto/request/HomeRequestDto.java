package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HomeRequestDto {

    @NotBlank(message = "집 이름을 입력하세요.")
    @Size(max = 50, message = "집 이름은 최대 50자까지 가능합니다.")
    private String homeName;

    @NotBlank(message = "주소를 입력하세요.")
    @Size(max = 100, message = "주소는 최대 100자까지 가능합니다.")
    private String address;
}