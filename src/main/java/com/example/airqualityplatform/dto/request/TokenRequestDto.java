package com.example.airqualityplatform.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 외부에서 24시간마다 전송해 주는 JSON 형태가
 * { "token": "d6ed3d63-5140-47b0-8229-cf068d827375" }
 * 라고 가정합니다.
 */
@Getter @Setter
public class TokenRequestDto {
    private String token;
}
