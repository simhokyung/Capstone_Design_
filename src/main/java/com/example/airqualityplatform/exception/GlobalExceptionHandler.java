package com.example.airqualityplatform.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: 잘못된 인자
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArg(IllegalArgumentException ex, WebRequest req) {
        var body = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "ERR_BAD_REQUEST",
                ex.getMessage(),
                req.getDescription(false)
        );
        return ResponseEntity.badRequest().body(body);
    }

    // 502: 외부 서비스 호출 실패 (SmartThings 등)
    @ExceptionHandler({ IllegalStateException.class, RestClientException.class })
    public ResponseEntity<ApiErrorResponse> handleExternalError(RuntimeException ex, WebRequest req) {
        var body = new ApiErrorResponse(
                HttpStatus.BAD_GATEWAY.value(),
                HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                "ERR_EXTERNAL_SERVICE",
                ex.getMessage(),
                req.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    // 409: 이메일 중복
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateResourceException ex, WebRequest req) {
        var body = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "ERR_DUPLICATE_EMAIL",
                ex.getMessage(),
                req.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 404: 리소스(사용자 등) 없음
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
        var body = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "ERR_NOT_FOUND",
                ex.getMessage(),
                req.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 401: 잘못된 로그인 정보
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCred(InvalidCredentialsException ex, WebRequest req) {
        var body = new ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "ERR_INVALID_CREDENTIALS",
                ex.getMessage(),
                req.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // 400: 요청 DTO 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a,b) -> a + ", " + b)
                .orElse(ex.getMessage());

        var body = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "ERR_VALIDATION_FAILED",
                detail,
                req.getDescription(false)
        );
        return ResponseEntity.badRequest().body(body);
    }

    // 401: JWT 토큰이 없거나 잘못됐을 때
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiErrorResponse> handleJwt(JwtException ex, WebRequest req) {
        var body = new ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "ERR_INVALID_TOKEN",
                ex.getMessage(),
                req.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // 403: 권한 부족
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest req) {
        var body = new ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "ERR_ACCESS_DENIED",
                "권한이 없습니다.",
                req.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // 500: 그 외 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAll(Exception ex, WebRequest req) {
        var body = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "ERR_INTERNAL_SERVER",
                ex.getMessage(),
                req.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
