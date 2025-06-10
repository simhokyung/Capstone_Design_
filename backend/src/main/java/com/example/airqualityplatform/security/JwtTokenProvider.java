package com.example.airqualityplatform.security;

import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 비밀키 (실제 배포 시에는 안전하게 관리, 환경변수나 외부 설정 사용)
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Access Token 유효기간 (예: 30분)->지금은 개발중이라 7일로 설정함.
    private final long accessTokenValidityInMillis  = 7L * 24 * 60 * 60 * 1000; // 7일
    // Refresh Token 유효기간 (예: 7일)
    private final long refreshTokenValidityInMillis = 7L * 24 * 60 * 60 * 1000;

    /**
     * Access Token 생성
     */
    public String createAccessToken(String userEmail, Long userId) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityInMillis);

        return Jwts.builder()
                .setSubject(userEmail)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(String userEmail, Long userId) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityInMillis);

        return Jwts.builder()
                .setSubject(userEmail)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰 검증 및 클레임 추출
     */
    public Jws<Claims> validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid or expired JWT token");
        }
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public Long getUserId(String token) {
        return ((Number) validateToken(token)
                .getBody()
                .get("userId"))
                .longValue();
    }

    /**
     * HttpServletRequest에서 지정한 헤더의 Bearer 토큰 부분만 리턴.
     * 예: "Authorization: Bearer eyJ..." → "eyJ..."
     */
    public String resolveToken(HttpServletRequest req, String headerName) {
        String bearer = req.getHeader(headerName);
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
