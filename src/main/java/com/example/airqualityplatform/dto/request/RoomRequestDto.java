package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RoomRequestDto {

    @NotBlank(message = "방 이름을 입력하세요.")
    @Size(max = 50, message = "방 이름은 최대 50자까지 가능합니다.")
    private String roomName;

    @NotBlank(message = "폴리곤 정보를 입력하세요.")
    private String polygon;

    private Integer floorNumber;

    @Size(max = 500, message = "설명은 최대 100자까지 가능합니다.")
    private String description;
}