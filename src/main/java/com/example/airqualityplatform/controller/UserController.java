package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.dto.response.UserResponseDto;
import com.example.airqualityplatform.service.UserService;
import com.example.airqualityplatform.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService        userService;
    private final JwtTokenProvider   jwtProvider;

    public UserController(UserService userService,
                          JwtTokenProvider jwtProvider) {
        this.userService   = userService;
        this.jwtProvider   = jwtProvider;
    }

    /**
     * GET /users/me
     * Authorization: Bearer {accessToken}
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication,
                                                          @RequestHeader("Authorization") String authHeader) {
        // 1) 헤더에서 토큰 꺼내기
        String token = authHeader.replaceFirst("^Bearer ", "");
        // 2) 토큰 검증 및 Claims 추출
        var claimsJws = jwtProvider.validateToken(token);
        String email = claimsJws.getBody().getSubject();
        Long   userId = ((Number) claimsJws.getBody().get("userId")).longValue();

        // 3) 서비스에서 UserResponseDto 가져오기
        UserResponseDto dto = userService.getById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));
        return ResponseEntity.ok(dto);
    }
}
