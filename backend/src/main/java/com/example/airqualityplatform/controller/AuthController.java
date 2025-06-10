package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.request.LoginRequest;
import com.example.airqualityplatform.dto.request.UserSignupRequest;
import com.example.airqualityplatform.dto.response.TokenResponse;
import com.example.airqualityplatform.dto.response.UserResponseDto;
import com.example.airqualityplatform.security.JwtTokenProvider;
import com.example.airqualityplatform.service.RefreshTokenService;
import com.example.airqualityplatform.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    /** 회원가입 */
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(
            @Valid @RequestBody UserSignupRequest req
    ) {
        UserResponseDto dto = userService.registerUser(req);
        return ResponseEntity.ok(dto);
    }

    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest req
    ) {
        // 서비스에도 authenticate(...) 메서드가 있어야 합니다.
        User user = userService.authenticate(req.getEmail(), req.getPassword());
        String accessToken  = jwtTokenProvider.createAccessToken(user.getEmail(), user.getUserId());
        String refreshToken = refreshTokenService.create(user).getToken();
        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

    /** 리프레시 토큰으로 엑세스 토큰 재발급 */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @RequestParam("refreshToken") String refreshToken
    ) {
        // 리프레시 토큰 검증
        refreshTokenService.validate(refreshToken);

        // JWT 파싱
        Jws<Claims> jws = jwtTokenProvider.validateToken(refreshToken);
        String email = jws.getBody().getSubject();
        Long userId  = jws.getBody().get("userId", Long.class);

        String newAccessToken = jwtTokenProvider.createAccessToken(email, userId);
        return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken));
    }

    /** 로그아웃: 리프레시 토큰 삭제 */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req) {
        // Authorization 헤더에서 “Bearer {token}” 을 꺼내오는 메서드를 JwtTokenProvider 에 구현하세요.
        String refreshToken = jwtTokenProvider.resolveToken(req, "Authorization");
        if (refreshToken != null) {
            // subject(email) 만 필요하니 password 빈 문자열 넘겨도 검사 통과하도록 authenticate 오버로드를 하나 만드시거나
            // 리프레시 토큰 저장소에서 직접 사용자 식별 후 deleteForUser 호출하세요.
            User user = userService.authenticate(
                    jwtTokenProvider.validateToken(refreshToken).getBody().getSubject(),
                    ""  // rawPassword unused here
            );
            refreshTokenService.deleteForUser(user);
        }
        return ResponseEntity.noContent().build();
    }
}
