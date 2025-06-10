package com.example.airqualityplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // ✅ 이거 꼭 추가해야 역직렬화 가능!

public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}
