package com.example.airqualityplatform.exception;

import lombok.Getter;
import java.time.Instant;

@Getter
public class ApiErrorResponse {
    private final Instant timestamp = Instant.now();
    private final int status;           // HTTP status
    private final String error;         // HTTP status reason
    private final String code;          // Application-specific error code
    private final String message;       // 사용자에게 보여줄 메시지
    private final String path;          // 요청 URI

    public ApiErrorResponse(int status, String error, String code, String message, String path) {
        this.status  = status;
        this.error   = error;
        this.code    = code;
        this.message = message;
        this.path    = path;
    }
}
